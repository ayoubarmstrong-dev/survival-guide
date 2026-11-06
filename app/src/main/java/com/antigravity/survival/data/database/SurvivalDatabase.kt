package com.antigravity.survival.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.antigravity.survival.data.dao.AffiliateGearDao
import com.antigravity.survival.data.dao.ArticleDao
import com.antigravity.survival.data.dao.CategoryDao
import com.antigravity.survival.data.dao.ChecklistDao
import com.antigravity.survival.data.entity.AffiliateGearEntity
import com.antigravity.survival.data.entity.ArticleEntity
import com.antigravity.survival.data.entity.CategoryEntity
import com.antigravity.survival.data.entity.ChecklistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(
    entities = [
        CategoryEntity::class,
        ArticleEntity::class,
        ChecklistEntity::class,
        AffiliateGearEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SurvivalDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun articleDao(): ArticleDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun affiliateGearDao(): AffiliateGearDao

    companion object {
        @Volatile
        private var INSTANCE: SurvivalDatabase? = null

        fun getDatabase(context: Context): SurvivalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SurvivalDatabase::class.java,
                    "survival_master.db"
                )
                    .createFromAsset("databases/survival_master.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun getDatabaseAsync(context: Context): SurvivalDatabase =
            withContext(Dispatchers.IO) {
                getDatabase(context)
            }
    }
}
