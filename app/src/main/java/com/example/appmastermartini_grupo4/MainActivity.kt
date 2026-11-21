package com.example.appmastermartini_grupo4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.appmastermartini_grupo4.ui.navigation.AppNavigation
import com.example.appmastermartini_grupo4.ui.theme.AppMasterMartini_Grupo4Theme

/**
 * [MainActivity]
 *
 * 1. Qué hace: Actividad principal que inicializa la interfaz de usuario con [AppNavigation] usando ViewModels seguros.
 * 2. Conexiones: Punto de entrada raíz que hospeda el grafo de navegación [AppNavigation].
 * 3. Rol en MVVM: Entry Point / UI Host.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppMasterMartini_Grupo4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
