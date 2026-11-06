package com.antigravity.survival.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.antigravity.survival.data.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles WHERE category_id = :categoryId ORDER BY rowid ASC")
    fun getArticlesByCategory(categoryId: String): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: String): ArticleEntity?

    @Query("SELECT * FROM articles WHERE title_ar LIKE '%' || :query || '%' OR content_markdown_ar LIKE '%' || :query || '%'")
    fun searchArticles(query: String): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE priority_level = 'HIGH' OR priority_level = 'CRITICAL' LIMIT 5")
    fun getHighPriorityArticles(): Flow<List<ArticleEntity>>
}
