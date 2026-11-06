package com.antigravity.survival.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "articles",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["category_id"])]
)
data class ArticleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "category_id")
    val categoryId: String,

    @ColumnInfo(name = "title_ar")
    val titleAr: String,

    @ColumnInfo(name = "title_en")
    val titleEn: String,

    @ColumnInfo(name = "summary_ar")
    val summaryAr: String,

    @ColumnInfo(name = "content_markdown_ar")
    val contentMarkdownAr: String,

    @ColumnInfo(name = "priority_level")
    val priorityLevel: String? = "NORMAL",

    @ColumnInfo(name = "diagram_asset")
    val diagramAsset: String? = null,

    @ColumnInfo(name = "is_offline_cached")
    val isOfflineCached: Int = 1
)
