package com.antigravity.survival.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.antigravity.survival.data.entity.AffiliateGearEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AffiliateGearDao {
    @Query("SELECT * FROM affiliate_gear WHERE article_id = :articleId ORDER BY importance_rating DESC")
    fun getGearForArticle(articleId: String): Flow<List<AffiliateGearEntity>>

    @Query("SELECT * FROM affiliate_gear ORDER BY importance_rating DESC")
    fun getAllGear(): Flow<List<AffiliateGearEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(gear: List<AffiliateGearEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(gear: AffiliateGearEntity)
}
