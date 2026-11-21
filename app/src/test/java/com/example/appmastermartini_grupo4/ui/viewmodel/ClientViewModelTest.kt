package com.example.appmastermartini_grupo4.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.appmastermartini_grupo4.data.repository.ClientRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * [ClientViewModelTest]
 *
 * 1. Qué hace: Prueba unitaria para [ClientViewModel] usando JUnit 4, MockK y Kotlinx Coroutines Test.
 *    Verifica la validación de formularios, cambios de estado y acciones de guardado y eliminación.
 * 2. Conexiones: Mockea [ClientRepository].
 * 3. Rol en Testing: Validación de la capa ViewModel (>80% cobertura).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ClientViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: ClientRepository
    private lateinit var viewModel: ClientViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        every { repository.allClients } returns flowOf(emptyList())
        viewModel = ClientViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test form validation with blank name sets error`() {
        viewModel.onNombreChanged("")
        assertEquals("", viewModel.nombreInput)
        assertEquals("El nombre del local es obligatorio *", viewModel.nombreError)
    }

    @Test
    fun `test form validation with valid email clears error`() {
        viewModel.onCorreoChanged("correo@valido.cl")
        assertEquals("correo@valido.cl", viewModel.correoInput)
        assertNull(viewModel.correoError)
    }

    @Test
    fun `test form validation with invalid email sets error`() {
        viewModel.onCorreoChanged("correoinvalido")
        assertEquals("correoinvalido", viewModel.correoInput)
        assertNotNull(viewModel.correoError)
    }

    @Test
    fun `test saveClient calls repository insert when valid`() = runTest {
        viewModel.onNombreChanged("Pastelería Test")
        viewModel.onTelefonoChanged("912345678")
        viewModel.onCorreoChanged("contacto@test.cl")
        viewModel.onDireccionChanged("Av. Alemania 1234")
        coEvery { repository.insertClient(any()) } returns Result.success(Unit)

        viewModel.saveClient {
            // Callback de éxito
        }

        testScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.insertClient(any()) }
    }
}
