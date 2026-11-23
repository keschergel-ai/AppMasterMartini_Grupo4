package com.example.appmastermartini_grupo4.ui.viewmodel

import com.example.appmastermartini_grupo4.data.repository.ExternalRepository
import com.example.appmastermartini_grupo4.model.ExternalRecipeModel
import com.example.appmastermartini_grupo4.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * [ExternalApiViewModelTest]
 *
 * 1. Qué hace: Prueba unitaria para [ExternalApiViewModel] usando JUnit 4, MockK y [MainDispatcherRule].
 *    Verifica la obtención de recetas/postres desde la API externa TheMealDB, el manejo de estados de carga, listas vacías y excepciones.
 * 2. Conexiones: Mockea la capa de repositorio de API externa [ExternalRepository].
 * 3. Rol en Testing: Garantiza que la UI de la API externa procese correctamente los resultados y errores.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExternalApiViewModelTest {

    // Regla para ejecutar corrutinas de manera síncrona en las pruebas
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mock del repositorio de la API externa
    private lateinit var repository: ExternalRepository

    // Instancia del ViewModel bajo prueba
    private lateinit var viewModel: ExternalApiViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
    }

    @Test
    fun `obtener recetas con exito actualiza la lista de recetas y limpia mensajes de error`() = runTest {
        // EXPLICACIÓN: Se mockea una búsqueda de postres/recetas exitosa ("cake").
        // SE VERIFICA: Que la lista recipes contenga los elementos retornados y errorMessage sea nulo.
        val recipeList = listOf(
            ExternalRecipeModel(
                idMeal = "52891",
                strMeal = "Pastel de Chocolate",
                strMealThumb = "https://www.themealdb.com/images/media/meals/1.jpg",
                strCategory = "Dessert",
                strArea = "British",
                strInstructions = "Mezclar y hornear."
            )
        )
        coEvery { repository.searchDesserts("cake") } returns recipeList

        viewModel = ExternalApiViewModel(repository)

        assertEquals(1, viewModel.recipes.size)
        assertEquals("Pastel de Chocolate", viewModel.recipes[0].strMeal)
        assertNull(viewModel.errorMessage)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `obtener recetas con lista vacia asigna un mensaje de error informativo`() = runTest {
        // EXPLICACIÓN: Se mockea una búsqueda que no retorna resultados en la API externa.
        // SE VERIFICA: Que se asigne un mensaje descriptivo de no encontrado en errorMessage.
        coEvery { repository.searchDesserts("recetaInexistente") } returns emptyList()

        viewModel = ExternalApiViewModel(repository)
        viewModel.fetchDesserts("recetaInexistente")

        assertTrue(viewModel.recipes.isEmpty())
        assertEquals("No se encontraron recetas para 'recetaInexistente'", viewModel.errorMessage)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `obtener recetas ante excepcion de red captura el error sin desplomar la aplicacion`() = runTest {
        // EXPLICACIÓN: Se simula un fallo de conexión a Internet al consultar TheMealDB.
        // SE VERIFICA: Que se capture la excepción y se guarde el mensaje en errorMessage.
        coEvery { repository.searchDesserts(any()) } throws RuntimeException("Error de red 500")

        viewModel = ExternalApiViewModel(repository)

        assertTrue(viewModel.recipes.isEmpty())
        assertNotNull(viewModel.errorMessage)
        assertTrue(viewModel.errorMessage!!.contains("Error al conectar con la API externa"))
        assertFalse(viewModel.isLoading)
    }
}
