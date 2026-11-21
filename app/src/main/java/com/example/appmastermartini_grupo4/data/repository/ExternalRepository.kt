package com.example.appmastermartini_grupo4.data.repository

import com.example.appmastermartini_grupo4.data.remote.ExternalApiService
import com.example.appmastermartini_grupo4.model.ExternalRecipeModel

/**
 * [ExternalRepository]
 *
 * 1. Qué hace: Gestiona la obtención de recetas y postres desde la API externa TheMealDB mediante [ExternalApiService].
 * 2. Conexiones: Conecta [ExternalApiService] con [ExternalApiViewModel].
 * 3. Rol en MVVM: Capa de Repositorio Externo.
 */
class ExternalRepository(
    private val externalApiService: ExternalApiService
) {

    suspend fun searchDesserts(query: String): List<ExternalRecipeModel> {
        return try {
            val response = externalApiService.searchRecipes(query)
            if (response.isSuccessful) {
                response.body()?.meals ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getDessertsByCategory(category: String): List<ExternalRecipeModel> {
        return try {
            val response = externalApiService.getRecipesByCategory(category)
            if (response.isSuccessful) {
                response.body()?.meals ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
