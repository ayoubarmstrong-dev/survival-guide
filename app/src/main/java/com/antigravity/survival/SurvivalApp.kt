package com.antigravity.survival

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.intercept.Interceptor
import coil.memory.MemoryCache
import coil.request.ErrorResult
import coil.request.ImageResult
import coil.request.SuccessResult

class SurvivalApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
                add(Interceptor { chain ->
                    val result = chain.proceed(chain.request)
                    if (result is SuccessResult) {
                        val drawable = result.drawable
                        // Filter out 1x1 transparent/tracking GIFs from CDN to trigger tactical vector fallback
                        if (drawable.intrinsicWidth <= 1 && drawable.intrinsicHeight <= 1) {
                            ErrorResult(
                                drawable = null,
                                request = chain.request,
                                throwable = IllegalStateException("1x1 blank image returned by CDN")
                            )
                        } else {
                            result
                        }
                    } else {
                        result
                    }
                })
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024) // 100 MB disk cache for offline tactical imagery
                    .build()
            }
            .respectCacheHeaders(false) // Cache images aggressively for offline field survival
            .crossfade(true)
            .build()
    }
}
