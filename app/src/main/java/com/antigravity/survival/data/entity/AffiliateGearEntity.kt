package com.antigravity.survival.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "affiliate_gear",
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["id"],
            childColumns = ["article_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["article_id"])]
)
data class AffiliateGearEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "article_id")
    val articleId: String?,

    @ColumnInfo(name = "name_ar")
    val nameAr: String,

    @ColumnInfo(name = "name_en")
    val nameEn: String,

    @ColumnInfo(name = "spec_summary_ar")
    val specSummaryAr: String,

    @ColumnInfo(name = "amazon_asin")
    val amazonAsin: String? = null,

    @ColumnInfo(name = "affiliate_url_template")
    val affiliateUrlTemplate: String,

    @ColumnInfo(name = "estimated_price_usd")
    val estimatedPriceUsd: Double? = null,

    @ColumnInfo(name = "image_asset")
    val imageAsset: String? = null,

    @ColumnInfo(name = "importance_rating")
    val importanceRating: Int = 5
)
