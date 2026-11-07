package com.antigravity.survival.data.util

import android.content.Context
import android.util.Log
import com.antigravity.survival.data.entity.AffiliateGearEntity
import com.antigravity.survival.data.entity.ArticleEntity
import com.antigravity.survival.data.entity.CategoryEntity
import com.antigravity.survival.data.entity.ChecklistEntity
import org.json.JSONObject

data class SurvivalBackupData(
    val categories: List<CategoryEntity>,
    val articles: List<ArticleEntity>,
    val checklists: List<ChecklistEntity>,
    val affiliateGear: List<AffiliateGearEntity>
)

object JsonDataLoader {
    private const val TAG = "JsonDataLoader"

    fun loadFromAssets(context: Context): SurvivalBackupData? {
        return try {
            val jsonString = context.assets.open("json/survival_master_content_en.json")
                .bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            val catList = mutableListOf<CategoryEntity>()
            val catArray = root.optJSONArray("categories")
            if (catArray != null) {
                for (i in 0 until catArray.length()) {
                    val obj = catArray.getJSONObject(i)
                    catList.add(
                        CategoryEntity(
                            id = obj.getString("id"),
                            nameAr = obj.optString("name", obj.optString("name_en", "")),
                            nameEn = obj.optString("name", obj.optString("name_en", "")),
                            iconName = obj.optString("icon_name", "ic_shield_check"),
                            sortOrder = obj.optInt("sort_order", i)
                        )
                    )
                }
            }

            val artList = mutableListOf<ArticleEntity>()
            val artArray = root.optJSONArray("articles")
            if (artArray != null) {
                for (i in 0 until artArray.length()) {
                    val obj = artArray.getJSONObject(i)
                    val diagramAsset = obj.optString("diagram_asset", "").let {
                        if (it.isBlank() || it == "null") null else it
                    }
                    artList.add(
                        ArticleEntity(
                            id = obj.getString("id"),
                            categoryId = obj.getString("category_id"),
                            titleAr = obj.optString("title", ""),
                            titleEn = obj.optString("title", ""),
                            summaryAr = obj.optString("summary", ""),
                            contentMarkdownAr = obj.optString("content_markdown", ""),
                            priorityLevel = obj.optString("priority_level", "NORMAL"),
                            diagramAsset = diagramAsset,
                            isOfflineCached = 1
                        )
                    )
                }
            }

            val chkList = mutableListOf<ChecklistEntity>()
            val chkArray = root.optJSONArray("checklists")
            if (chkArray != null) {
                for (i in 0 until chkArray.length()) {
                    val obj = chkArray.getJSONObject(i)
                    val formula = obj.optString("quantity_formula", "").let {
                        if (it.isBlank() || it == "null") null else it
                    }
                    val articleId = obj.optString("article_id", "").let {
                        if (it.isBlank() || it == "null") null else it
                    }
                    chkList.add(
                        ChecklistEntity(
                            id = obj.getString("id"),
                            articleId = articleId,
                            titleAr = obj.optString("title", ""),
                            itemAr = obj.optString("specification", obj.optString("title", "")),
                            quantityFormula = formula,
                            isMandatory = obj.optInt("is_mandatory", 1)
                        )
                    )
                }
            }

            val gearList = mutableListOf<AffiliateGearEntity>()
            val gearArray = root.optJSONArray("affiliate_gear")
            if (gearArray != null) {
                for (i in 0 until gearArray.length()) {
                    val obj = gearArray.getJSONObject(i)
                    val asin = obj.optString("amazon_asin", "").let {
                        if (it.isBlank() || it == "null") null else it
                    }
                    val articleId = obj.optString("article_id", "").let {
                        if (it.isBlank() || it == "null") null else it
                    }
                    val imgAsset = obj.optString("image_asset", "").let {
                        if (it.isBlank() || it == "null") null else it
                    }
                    val imgUrl = obj.optString("image_url", "").let {
                        if (it.isBlank() || it == "null") null else it
                    }
                    val price = if (obj.has("estimated_price_usd")) obj.optDouble("estimated_price_usd") else null
                    val category = obj.optString("category", "Tactical Gear")
                    val priorityTier = obj.optString("priority_tier", "Field Ops")
                    gearList.add(
                        AffiliateGearEntity(
                            id = obj.getString("id"),
                            articleId = articleId,
                            nameAr = obj.optString("name", ""),
                            nameEn = obj.optString("name", ""),
                            specSummaryAr = obj.optString("spec_summary", ""),
                            amazonAsin = asin,
                            affiliateUrlTemplate = obj.getString("affiliate_url_template"),
                            estimatedPriceUsd = price,
                            imageAsset = imgAsset,
                            importanceRating = obj.optInt("importance_rating", 5),
                            category = category,
                            priorityTier = priorityTier,
                            imageUrl = imgUrl
                        )
                    )
                }
            }

            SurvivalBackupData(catList, artList, chkList, gearList)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load backup data from JSON assets", e)
            null
        }
    }
}
