package com.antigravity.survival.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.antigravity.survival.data.entity.ChecklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklists WHERE article_id = :articleId")
    fun getChecklistsForArticle(articleId: String): Flow<List<ChecklistEntity>>

    @Query("SELECT * FROM checklists")
    fun getAllChecklists(): Flow<List<ChecklistEntity>>
}
