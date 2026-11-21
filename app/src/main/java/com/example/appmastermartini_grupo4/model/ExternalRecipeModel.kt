package com.example.appmastermartini_grupo4.model

import com.google.gson.annotations.SerializedName

/**
 * [ExternalRecipeResponse] y [ExternalRecipeModel]
 *
 * 1. Qué hace: Define la estructura de datos para mapear las respuestas JSON de la API externa (TheMealDB - Panadería y Postres).
 * 2. Conexiones: Es consumida por [ExternalApiService] y gestionada por [ExternalApiViewModel].
 * 3. Rol en MVVM: Capa de Modelo Remoto independiente para catálogos y recetas de terceros.
 */
data class ExternalRecipeResponse(
    @SerializedName("meals")
    val meals: List<ExternalRecipeModel>?
)

data class ExternalRecipeModel(
    @SerializedName("idMeal")
    val idMeal: String,
    @SerializedName("strMeal")
    val strMeal: String,
    @SerializedName("strCategory")
    val strCategory: String?,
    @SerializedName("strArea")
    val strArea: String?,
    @SerializedName("strInstructions")
    val strInstructions: String?,
    @SerializedName("strMealThumb")
    val strMealThumb: String?
)
