package com.antigravity.survival.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.work.*
import com.antigravity.survival.data.database.SurvivalDatabase
import com.antigravity.survival.data.entity.AffiliateGearEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * DataSyncWorker: Remote Fetch Worker for Aegis Survival.
 * Fetches the latest gear catalog from GitHub Pages and upserts into local Room DB.
 * Implements strict offline-first resilience: times out fast and falls back to cached DB without crashing.
 */
class DataSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "DataSyncWorker"
        const val ENDPOINT_URL = "https://ayoubarmstrong-dev.github.io/survival-guide/api/gear.json"
        const val WORK_NAME_PERIODIC = "aegis_data_sync_periodic"
        const val WORK_NAME_ONE_TIME = "aegis_data_sync_one_time"

        /**
         * Schedules periodic background sync (every 12 hours) when unmetered or connected.
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val periodicRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
                12, TimeUnit.HOURS,
                1, TimeUnit.HOURS // Flex interval
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
            Log.d(TAG, "Scheduled periodic DataSyncWorker.")
        }

        /**
         * Triggers an immediate one-time sync attempt (e.g. on app launch or manual refresh).
         */
        fun syncImmediately(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val oneTimeRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_ONE_TIME,
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest
            )
            Log.d(TAG, "Enqueued immediate one-time DataSyncWorker.")
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (!isOnline(applicationContext)) {
            Log.d(TAG, "Offline mode detected: Skipping remote fetch, serving cached Room database.")
            return@withContext Result.success()
        }

        try {
            Log.i(TAG, "Connecting to remote catalog endpoint: $ENDPOINT_URL")
            val url = URL(ENDPOINT_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000 // 8-second connect timeout
                readTimeout = 8000    // 8-second read timeout
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "AegisSurvival-AndroidApp/1.0")
                instanceFollowRedirects = true
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "Server responded with HTTP $responseCode. Keeping local Room cache.")
                connection.disconnect()
                return@withContext Result.success()
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
            val responseText = reader.use { it.readText() }
            connection.disconnect()

            val gearList = parseGearJson(responseText)
            if (gearList.isNotEmpty()) {
                val db = SurvivalDatabase.getDatabase(applicationContext)
                db.affiliateGearDao().upsertAll(gearList)
                Log.i(TAG, "Successfully synced & upserted ${gearList.size} gear items into Room database.")
            } else {
                Log.w(TAG, "Remote endpoint returned empty gear list. Preserving local cache.")
            }

            Result.success()
        } catch (e: Exception) {
            // Gracefully catch timeouts, host resolution errors, and parsing issues
            Log.w(TAG, "Remote sync gracefully failed (network timeout or unreachable). Serving local Room database.", e)
            Result.success()
        }
    }

    private fun parseGearJson(jsonStr: String): List<AffiliateGearEntity> {
        val result = mutableListOf<AffiliateGearEntity>()
        val trimmed = jsonStr.trim()
        val jsonArray = if (trimmed.startsWith("[")) {
            JSONArray(trimmed)
        } else {
            val rootObj = JSONObject(trimmed)
            rootObj.optJSONArray("gear")
                ?: rootObj.optJSONArray("affiliate_gear")
                ?: JSONArray()
        }

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val id = item.optString("id")
            if (id.isBlank()) continue

            val name = item.optString("name", item.optString("name_en", "Survival Gear"))
            val spec = item.optString("spec_summary", item.optString("spec_summary_ar", ""))
            val price = if (item.has("estimated_price_usd") && !item.isNull("estimated_price_usd")) {
                item.optDouble("estimated_price_usd")
            } else null

            val articleId = item.optString("article_id", "").let {
                if (it.isBlank() || it == "null") null else it
            }

            val asin = item.optString("amazon_asin", "").let {
                if (it.isBlank() || it == "null") null else it
            }

            val url = item.optString("affiliate_url_template", "https://www.amazon.com/dp/$asin?tag=aegissurvival-20")
            val rating = item.optInt("importance_rating", 5)

            val imgAsset = if (item.has("image_asset") && !item.isNull("image_asset")) {
                item.optString("image_asset").takeIf { it.isNotBlank() }
            } else null

            val category = item.optString("category", "Tactical Gear")
            val priorityTier = item.optString("priority_tier", "Field Ops")

            result.add(
                AffiliateGearEntity(
                    id = id,
                    articleId = articleId,
                    nameAr = name,
                    nameEn = name,
                    specSummaryAr = spec,
                    amazonAsin = asin,
                    affiliateUrlTemplate = url,
                    estimatedPriceUsd = price,
                    imageAsset = imgAsset,
                    importanceRating = rating,
                    category = category,
                    priorityTier = priorityTier
                )
            )
        }

        return result
    }

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
