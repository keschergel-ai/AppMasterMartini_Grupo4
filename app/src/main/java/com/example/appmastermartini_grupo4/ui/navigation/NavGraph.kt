package com.example.appmastermartini_grupo4.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appmastermartini_grupo4.ui.screens.DetailScreen
import com.example.appmastermartini_grupo4.ui.screens.ExternalApiScreen
import com.example.appmastermartini_grupo4.ui.screens.FormScreen
import com.example.appmastermartini_grupo4.ui.screens.HomeScreen
import com.example.appmastermartini_grupo4.ui.screens.LoginScreen
import com.example.appmastermartini_grupo4.ui.screens.WelcomeScreen
import com.example.appmastermartini_grupo4.ui.viewmodel.ClientViewModel
import com.example.appmastermartini_grupo4.ui.viewmodel.ExternalApiViewModel

/**
 * [NavGraph]
 *
 * 1. Qué hace: Configura el grafo de navegación de 6 pantallas en Jetpack Compose (`NavHost`), gestionando el flujo desde la bienvenida hasta el CRUD y la API externa.
 * 2. Conexiones: Conecta [WelcomeScreen], [LoginScreen], [HomeScreen], [FormScreen], [DetailScreen] y [ExternalApiScreen] con los ViewModels.
 * 3. Rol en MVVM: Capa de Navegación UI.
 */
@Composable
fun NavGraph(
    clientViewModel: ClientViewModel,
    externalApiViewModel: ExternalApiViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        // Pantalla 1: WelcomeScreen
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        // Pantalla 2: LoginScreen
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla 3: HomeScreen
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = clientViewModel,
                onNavigateToForm = { navController.navigate(Screen.Form.route) },
                onNavigateToDetail = { clientId -> navController.navigate(Screen.Detail.createRoute(clientId)) },
                onNavigateToExternalApi = { navController.navigate(Screen.ExternalApi.route) }
            )
        }

        // Pantalla 4: FormScreen
        composable(Screen.Form.route) {
            FormScreen(
                viewModel = clientViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla 5: DetailScreen
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("clientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt("clientId") ?: 0
            DetailScreen(
                clientId = clientId,
                viewModel = clientViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Screen.Form.route) }
            )
        }

        // Pantalla 6: ExternalApiScreen
        composable(Screen.ExternalApi.route) {
            ExternalApiScreen(
                viewModel = externalApiViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
