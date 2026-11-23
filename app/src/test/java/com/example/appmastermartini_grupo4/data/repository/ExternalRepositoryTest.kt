package com.example.appmastermartini_grupo4.data.repository

import com.example.appmastermartini_grupo4.data.remote.ExternalApiService
import com.example.appmastermartini_grupo4.model.ExternalRecipeModel
import com.example.appmastermartini_grupo4.model.ExternalRecipeResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * [ExternalRepositoryTest]
 *
 * 1. Qué hace: Pruebas unitarias para [ExternalRepository] usando JUnit 4 y MockK.
 *    Verifica las llamadas a los endpoints de Retrofit de TheMealDB para búsqueda por nombre y por categoría.
 * 2. Conexiones: Mockea la interfaz de servicio HTTP [ExternalApiService].
 * 3. Rol en Testing: Validación de la capa de datos de repositorios externos.
 */
class ExternalRepositoryTest {

    private lateinit var externalApiService: ExternalApiService
    private lateinit var repository: ExternalRepository

    @Before
    fun setUp() {
        externalApiService = mockk(relaxed = true)
        repository = ExternalRepository(externalApiService)
    }

    @Test
    fun `buscar postres por nombre retorna lista de recetas cuando la respuesta HTTP es exitosa`() = runTest {
        // EXPLICACIÓN: Se simula una respuesta 200 OK con una lista de recetas devuelta por la API pública.
        // SE VERIFICA: Que searchDesserts devuelva la lista de recetas correctamente.
        val recipe = ExternalRecipeModel(
            idMeal = "1",
            strMeal = "Pie de Limón",
            strCategory = "Dessert",
            strArea = "Chilean",
            strInstructions = "Mezclar e ingrediente",
            strMealThumb = "http://thumb.jpg"
        )
        val response = ExternalRecipeResponse(meals = listOf(recipe))
        coEvery { externalApiService.searchRecipes("pie") } returns Response.success(response)

        val result = repository.searchDesserts("pie")

        assertEquals(1, result.size)
        assertEquals("Pie de Limón", result[0].strMeal)
    }

    @Test
    fun `buscar postres ante error de servidor retorna lista vacia de forma segura`() = runTest {
        // EXPLICACIÓN: Se simula un error HTTP o una excepción al invocar Retrofit.
        // SE VERIFICA: Que el repositorio retorne una lista vacía sin propagar excepciones no controladas.
        coEvery { externalApiService.searchRecipes(any()) } throws RuntimeException("Error de red")

        val result = repository.searchDesserts("error")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `obtener recetas por categoria retorna lista cuando la respuesta es exitosa`() = runTest {
        // EXPLICACIÓN: Se simula la consulta del endpoint filter.php por categoría (Dessert).
        // SE VERIFICA: Que getDessertsByCategory entregue la lista de recetas correspondiente.
        val recipe = ExternalRecipeModel(
            idMeal = "2",
            strMeal = "Tartaleta de Fruta",
            strCategory = "Dessert",
            strArea = "Chilean",
            strInstructions = "Hornear",
            strMealThumb = "http://thumb2.jpg"
        )
        val response = ExternalRecipeResponse(meals = listOf(recipe))
        coEvery { externalApiService.getRecipesByCategory("Dessert") } returns Response.success(response)

        val result = repository.getDessertsByCategory("Dessert")

        assertEquals(1, result.size)
        assertEquals("Tartaleta de Fruta", result[0].strMeal)
    }
}
