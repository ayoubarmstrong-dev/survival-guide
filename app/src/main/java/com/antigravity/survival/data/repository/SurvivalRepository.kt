package com.antigravity.survival.data.repository

import com.antigravity.survival.data.database.SurvivalDatabase
import com.antigravity.survival.data.entity.AffiliateGearEntity
import com.antigravity.survival.data.entity.ArticleEntity
import com.antigravity.survival.data.entity.CategoryEntity
import com.antigravity.survival.data.entity.ChecklistEntity
import kotlinx.coroutines.flow.Flow

class SurvivalRepository(private val database: SurvivalDatabase) {

    fun getCategories(): Flow<List<CategoryEntity>> =
        database.categoryDao().getAllCategories()

    suspend fun getCategoryById(id: String): CategoryEntity? =
        database.categoryDao().getCategoryById(id)

    fun getArticlesByCategory(categoryId: String): Flow<List<ArticleEntity>> =
        database.articleDao().getArticlesByCategory(categoryId)

    suspend fun getArticleById(id: String): ArticleEntity? =
        database.articleDao().getArticleById(id)

    fun searchArticles(query: String): Flow<List<ArticleEntity>> =
        database.articleDao().searchArticles(query)

    fun getHighPriorityArticles(): Flow<List<ArticleEntity>> =
        database.articleDao().getHighPriorityArticles()

    fun getChecklistsForArticle(articleId: String): Flow<List<ChecklistEntity>> =
        database.checklistDao().getChecklistsForArticle(articleId)

    fun getGearForArticle(articleId: String): Flow<List<AffiliateGearEntity>> =
        database.affiliateGearDao().getGearForArticle(articleId)

    fun getAllGear(): Flow<List<AffiliateGearEntity>> =
        database.affiliateGearDao().getAllGear()

    suspend fun upsertGear(gear: List<AffiliateGearEntity>) {
        database.affiliateGearDao().upsertAll(gear)
    }
}
