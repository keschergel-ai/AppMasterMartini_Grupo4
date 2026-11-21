package com.example.appmastermartini_grupo4.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmastermartini_grupo4.ui.viewmodel.ClientViewModel
import com.example.appmastermartini_grupo4.ui.viewmodel.ExternalApiViewModel

/**
 * [AppNavigation]
 *
 * 1. Qué hace: Punto de entrada de navegación de la aplicación con instanciación segura de ViewModels mediante [ViewModelProvider.Factory] (`viewModel(factory = ...)`).
 * 2. Conexiones: Conecta [MainActivity] con el grafo de navegación [NavGraph], resolviendo el error `Cannot create an instance of class ClientViewModel`.
 * 3. Rol en MVVM: Capa de Navegación / ViewModel Factory Provider.
 */
@Composable
fun AppNavigation(
    clientViewModel: ClientViewModel = viewModel(
        modelClass = ClientViewModel::class.java,
        factory = ClientViewModel.Factory(LocalContext.current),
    ),
    externalApiViewModel: ExternalApiViewModel = viewModel(
        modelClass = ExternalApiViewModel::class.java,
        factory = ExternalApiViewModel.Factory(),
    ),
) {
    NavGraph(
        clientViewModel = clientViewModel,
        externalApiViewModel = externalApiViewModel
    )
}
