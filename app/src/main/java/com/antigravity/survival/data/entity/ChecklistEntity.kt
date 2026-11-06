package com.antigravity.survival.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "checklists",
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["id"],
            childColumns = ["article_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["article_id"])]
)
data class ChecklistEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "article_id")
    val articleId: String?,

    @ColumnInfo(name = "title_ar")
    val titleAr: String,

    @ColumnInfo(name = "item_ar")
    val itemAr: String,

    @ColumnInfo(name = "quantity_formula")
    val quantityFormula: String? = null,

    @ColumnInfo(name = "is_mandatory")
    val isMandatory: Int = 1
)
