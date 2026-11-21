package com.example.appmastermartini_grupo4.data.remote

import com.example.appmastermartini_grupo4.model.ExternalRecipeResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * [ExternalApiService]
 *
 * 1. Qué hace: Cliente Retrofit independiente para consumir la API pública TheMealDB (recetas, panadería y postres).
 * 2. Conexiones: Invocado por [ExternalRepository].
 * 3. Rol en MVVM: Capa de Datos Externa (API pública de terceros).
 */
interface ExternalApiService {

    @GET("api/json/v1/1/search.php")
    suspend fun searchRecipes(@Query("s") query: String): Response<ExternalRecipeResponse>

    @GET("api/json/v1/1/filter.php")
    suspend fun getRecipesByCategory(@Query("c") category: String): Response<ExternalRecipeResponse>

    companion object {
        private const val BASE_URL = "https://www.themealdb.com/"

        fun create(): ExternalApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ExternalApiService::class.java)
        }
    }
}
