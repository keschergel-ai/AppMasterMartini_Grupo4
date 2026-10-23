package com.example.appmastermartini_grupo4.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object Detail : Screen("detail_screen/{recipeTitle}") {
        fun createRoute(recipeTitle: String) = "detail_screen/$recipeTitle"
    }
    data object Form : Screen("form_screen")
}