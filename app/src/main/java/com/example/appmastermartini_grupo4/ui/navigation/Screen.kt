package com.example.appmastermartini_grupo4.ui.navigation

/**
 * [Screen]
 *
 * 1. Qué hace: Define las rutas selladas (sealed class) para la navegación de las 6 pantallas de la aplicación en Jetpack Compose.
 * 2. Conexiones: Utilizado por [NavGraph] y las pantallas ([WelcomeScreen], [LoginScreen], [HomeScreen], [FormScreen], [DetailScreen], [ExternalApiScreen]).
 * 3. Rol en MVVM: Capa de Navegación UI.
 */
sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome_screen")
    data object Login : Screen("login_screen")
    data object Home : Screen("home_screen")
    data object Form : Screen("form_screen")
    data object Detail : Screen("detail_screen/{clientId}") {
        fun createRoute(clientId: Int) = "detail_screen/$clientId"
    }
    data object ExternalApi : Screen("external_api_screen")
}
