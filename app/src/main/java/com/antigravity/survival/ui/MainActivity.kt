package com.antigravity.survival.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.antigravity.survival.ui.screens.*
import com.antigravity.survival.ui.theme.AmoledBlack
import com.antigravity.survival.ui.theme.SurvivalTheme
import com.antigravity.survival.ui.viewmodel.SurvivalViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SurvivalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurvivalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AmoledBlack
                ) {
                    SurvivalAppNavigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun SurvivalAppNavigation(viewModel: SurvivalViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onCategoryClick = { categoryId, categoryTitle ->
                    viewModel.selectCategory(categoryId)
                    val encodedTitle = java.net.URLEncoder.encode(categoryTitle, "UTF-8")
                    navController.navigate("category_articles/$categoryId/$encodedTitle")
                },
                onEmergencyHudClick = {
                    navController.navigate("emergency_hud")
                },
                onCalculatorClick = {
                    navController.navigate("ration_calculator")
                },
                onStoreClick = {
                    navController.navigate("gear_store")
                },
                onArticleClick = { articleId ->
                    navController.navigate("article_detail/$articleId")
                }
            )
        }

        composable(
            route = "category_articles/{categoryId}/{categoryTitle}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("categoryTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val rawTitle = backStackEntry.arguments?.getString("categoryTitle") ?: ""
            val categoryTitle = try {
                java.net.URLDecoder.decode(rawTitle, "UTF-8")
            } catch (_: Exception) {
                rawTitle
            }
            CategoryArticlesScreen(
                categoryTitle = categoryTitle,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onArticleClick = { articleId ->
                    navController.navigate("article_detail/$articleId")
                }
            )
        }

        composable(
            route = "article_detail/{articleId}",
            arguments = listOf(
                navArgument("articleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
            ArticleDetailScreen(
                articleId = articleId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("ration_calculator") {
            RationCalculatorScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("emergency_hud") {
            EmergencyHudScreen(
                onBackClick = { navController.popBackStack() },
                onOpenArticle = { articleId ->
                    navController.navigate("article_detail/$articleId")
                }
            )
        }

        composable("gear_store") {
            GearStoreScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
