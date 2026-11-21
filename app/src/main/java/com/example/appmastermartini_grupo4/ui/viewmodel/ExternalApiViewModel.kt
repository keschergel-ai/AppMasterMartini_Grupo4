package com.example.appmastermartini_grupo4.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appmastermartini_grupo4.data.remote.ExternalApiService
import com.example.appmastermartini_grupo4.data.repository.ExternalRepository
import com.example.appmastermartini_grupo4.model.ExternalRecipeModel
import kotlinx.coroutines.launch

/**
 * [ExternalApiViewModel]
 *
 * 1. Qué hace: Gestiona el estado de la UI y las peticiones a la API externa de recetas y postres ([ExternalRepository]).
 * 2. Conexiones: Conecta [ExternalRepository] con [ExternalApiScreen].
 * 3. Rol en MVVM: ViewModel para datos externos independientes.
 */
class ExternalApiViewModel(
    private val externalRepository: ExternalRepository
) : ViewModel() {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val externalApi = ExternalApiService.create()
            val repository = ExternalRepository(externalApi)
            return ExternalApiViewModel(repository) as T
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = Factory()
    }

    var recipes by mutableStateOf<List<ExternalRecipeModel>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchDesserts("cake") // Carga inicial por defecto (pasteles / panadería)
    }

    fun fetchDesserts(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val results = externalRepository.searchDesserts(query)
                recipes = results
                if (results.isEmpty()) {
                    errorMessage = "No se encontraron recetas para '$query'"
                }
            } catch (e: Exception) {
                errorMessage = "Error al conectar con la API externa: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
