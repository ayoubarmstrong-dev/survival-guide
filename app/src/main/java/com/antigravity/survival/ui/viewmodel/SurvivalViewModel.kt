package com.antigravity.survival.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.survival.data.calculator.RationCalculationResult
import com.antigravity.survival.data.calculator.RationCalculator
import com.antigravity.survival.data.database.SurvivalDatabase
import com.antigravity.survival.data.entity.AffiliateGearEntity
import com.antigravity.survival.data.entity.ArticleEntity
import com.antigravity.survival.data.entity.CategoryEntity
import com.antigravity.survival.data.entity.ChecklistEntity
import com.antigravity.survival.data.repository.SurvivalRepository
import com.antigravity.survival.data.util.JsonDataLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class SurvivalViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "SurvivalViewModel"
    private var repository: SurvivalRepository? = null

    // In-memory fallback caches
    private var cachedArticles: List<ArticleEntity> = emptyList()
    private var cachedChecklists: List<ChecklistEntity> = emptyList()
    private var cachedGear: List<AffiliateGearEntity> = emptyList()

    // Categories
    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    // High Priority Articles
    private val _highPriorityArticles = MutableStateFlow<List<ArticleEntity>>(emptyList())
    val highPriorityArticles: StateFlow<List<ArticleEntity>> = _highPriorityArticles.asStateFlow()

    // Category Articles
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _categoryArticles = MutableStateFlow<List<ArticleEntity>>(emptyList())
    val categoryArticles: StateFlow<List<ArticleEntity>> = _categoryArticles.asStateFlow()

    // Current Article
    private val _currentArticle = MutableStateFlow<ArticleEntity?>(null)
    val currentArticle: StateFlow<ArticleEntity?> = _currentArticle.asStateFlow()

    // Checklists for Current Article
    private val _articleChecklists = MutableStateFlow<List<ChecklistEntity>>(emptyList())
    val articleChecklists: StateFlow<List<ChecklistEntity>> = _articleChecklists.asStateFlow()

    // Gear for Current Article
    private val _articleGear = MutableStateFlow<List<AffiliateGearEntity>>(emptyList())
    val articleGear: StateFlow<List<AffiliateGearEntity>> = _articleGear.asStateFlow()

    // All Tactical Gear for dedicated GearStoreScreen
    private val _allGear = MutableStateFlow<List<AffiliateGearEntity>>(emptyList())
    val allGear: StateFlow<List<AffiliateGearEntity>> = _allGear.asStateFlow()

    // Interactive checklist states (checked item IDs)
    private val _checkedItemIds = MutableStateFlow<Set<String>>(emptySet())
    val checkedItemIds: StateFlow<Set<String>> = _checkedItemIds.asStateFlow()

    // Ration Calculator State
    private val _adultsCount = MutableStateFlow(2)
    val adultsCount: StateFlow<Int> = _adultsCount.asStateFlow()

    private val _childrenCount = MutableStateFlow(1)
    val childrenCount: StateFlow<Int> = _childrenCount.asStateFlow()

    private val _daysCount = MutableStateFlow(14)
    val daysCount: StateFlow<Int> = _daysCount.asStateFlow()

    private val _calculationResult = MutableStateFlow(
        RationCalculator.calculate(2, 1, 14)
    )
    val calculationResult: StateFlow<RationCalculationResult> = _calculationResult.asStateFlow()

    init {
        // Asynchronous initialization on Dispatchers.IO - NEVER blocks main thread
        viewModelScope.launch(Dispatchers.IO) {
            // Step 1: Pre-populate immediately from JSON asset fallback
            try {
                val backup = JsonDataLoader.loadFromAssets(application)
                if (backup != null) {
                    cachedArticles = backup.articles
                    cachedChecklists = backup.checklists
                    cachedGear = backup.affiliateGear

                    _categories.value = backup.categories
                    _allGear.value = backup.affiliateGear
                    _highPriorityArticles.value = backup.articles.filter {
                        it.priorityLevel?.equals("CRITICAL", ignoreCase = true) == true ||
                                it.priorityLevel?.equals("HIGH", ignoreCase = true) == true
                    }.take(5)
                }
            } catch (e: Exception) {
                Log.e(tag, "Initial JSON asset load warning", e)
            }

            // Step 2: Initialize Room Database asynchronously on Dispatchers.IO
            try {
                val db = SurvivalDatabase.getDatabaseAsync(application)
                val repo = SurvivalRepository(db)
                repository = repo

                // Collect categories from Room
                launch {
                    try {
                        repo.getCategories().collect { dbCats ->
                            if (dbCats.isNotEmpty()) {
                                _categories.value = dbCats
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Room getCategories collect error", e)
                    }
                }

                // Collect high-priority articles from Room
                launch {
                    try {
                        repo.getHighPriorityArticles().collect { dbArts ->
                            if (dbArts.isNotEmpty()) {
                                _highPriorityArticles.value = dbArts
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Room getHighPriorityArticles collect error", e)
                    }
                }

                // Collect all gear from Room
                launch {
                    try {
                        repo.getAllGear().collect { dbGear ->
                            if (dbGear.isNotEmpty()) {
                                _allGear.value = dbGear
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Room getAllGear collect error", e)
                    }
                }

            } catch (e: Exception) {
                Log.e(tag, "Room database init failed, continuing in JSON mode", e)
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
        viewModelScope.launch(Dispatchers.IO) {
            val repo = repository
            if (repo != null) {
                try {
                    repo.getArticlesByCategory(categoryId).collect { articles ->
                        _categoryArticles.value = articles
                    }
                    return@launch
                } catch (e: Exception) {
                    Log.e(tag, "Error loading articles from Room for category $categoryId", e)
                }
            }
            // Fallback to cached articles
            _categoryArticles.value = cachedArticles.filter { it.categoryId == categoryId }
        }
    }

    fun loadArticle(articleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val repo = repository
            var article: ArticleEntity? = null
            if (repo != null) {
                try {
                    article = repo.getArticleById(articleId)
                } catch (e: Exception) {
                    Log.e(tag, "Error loading article $articleId from Room", e)
                }
            }
            if (article == null) {
                article = cachedArticles.find { it.id == articleId }
            }
            _currentArticle.value = article

            // Load checklists
            if (repo != null) {
                try {
                    repo.getChecklistsForArticle(articleId).collect {
                        _articleChecklists.value = it
                    }
                } catch (e: Exception) {
                    _articleChecklists.value = cachedChecklists.filter { it.articleId == articleId }
                }
            } else {
                _articleChecklists.value = cachedChecklists.filter { it.articleId == articleId }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val repo = repository
            if (repo != null) {
                try {
                    repo.getGearForArticle(articleId).collect {
                        _articleGear.value = it
                    }
                } catch (e: Exception) {
                    _articleGear.value = cachedGear.filter { it.articleId == articleId }
                }
            } else {
                _articleGear.value = cachedGear.filter { it.articleId == articleId }
            }
        }
    }

    fun toggleChecklistItem(itemId: String) {
        val current = _checkedItemIds.value.toMutableSet()
        if (current.contains(itemId)) {
            current.remove(itemId)
        } else {
            current.add(itemId)
        }
        _checkedItemIds.value = current
    }

    fun updateRationParams(adults: Int, children: Int, days: Int) {
        _adultsCount.value = adults.coerceAtLeast(1)
        _childrenCount.value = children.coerceAtLeast(0)
        _daysCount.value = days.coerceAtLeast(1)
        _calculationResult.value = RationCalculator.calculate(
            _adultsCount.value,
            _childrenCount.value,
            _daysCount.value
        )
    }
}
