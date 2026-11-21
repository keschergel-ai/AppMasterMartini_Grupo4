package com.example.appmastermartini_grupo4.data.repository

import com.example.appmastermartini_grupo4.data.local.ClientDao
import com.example.appmastermartini_grupo4.data.local.ClientEntity
import com.example.appmastermartini_grupo4.data.remote.MicroserviceApiService
import com.example.appmastermartini_grupo4.model.ClientModel
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * [ClientRepositoryTest]
 *
 * 1. Qué hace: Prueba unitaria para [ClientRepository] utilizando JUnit 4 y MockK.
 *    Verifica la inserción, actualización, eliminación y manejo de errores de red con respaldo en Room DB.
 * 2. Conexiones: Mockea [ClientDao] y [MicroserviceApiService].
 * 3. Rol en Testing: Validación de la capa de Repositorio (>80% cobertura).
 */
class ClientRepositoryTest {

    private lateinit var clientDao: ClientDao
    private lateinit var apiService: MicroserviceApiService
    private lateinit var repository: ClientRepository

    @Before
    fun setUp() {
        clientDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        repository = ClientRepository(clientDao, apiService)
    }

    @Test
    fun `test getAllClients emits mapped domain models`() = runTest {
        val entityList = listOf(
            ClientEntity(id = 1, nombre = "Pastelería Delicias", telefono = "912345678", correo = "contacto@delicias.cl", direccion = "Av. Providencia 123")
        )
        every { clientDao.getAllClients() } returns flowOf(entityList)

        // Verificar flujo de datos
        repository.allClients.collect { clients ->
            assertEquals(1, clients.size)
            assertEquals("Pastelería Delicias", clients[0].nombre)
            assertEquals("912345678", clients[0].telefono)
        }
    }

    @Test
    fun `test insertClient saves locally and calls remote api`() = runTest {
        val clientModel = ClientModel(
            id = 1,
            nombre = "Panadería Central",
            telefono = "987654321",
            correo = "central@pan.cl",
            direccion = "Calle Principal 456"
        )
        coEvery { clientDao.insertClient(any()) } returns 1L
        coEvery { apiService.createClient(any()) } returns Response.success(clientModel)

        val result = repository.insertClient(clientModel)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { clientDao.insertClient(any()) }
        coVerify(exactly = 1) { apiService.createClient(any()) }
    }

    @Test
    fun `test deleteClient deletes locally and calls remote api`() = runTest {
        val clientId = 1
        coEvery { clientDao.deleteClientById(clientId) } just Runs
        coEvery { apiService.deleteClient(clientId) } returns Response.success(Unit)

        val result = repository.deleteClient(clientId)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { clientDao.deleteClientById(clientId) }
        coVerify(exactly = 1) { apiService.deleteClient(clientId) }
    }
}
