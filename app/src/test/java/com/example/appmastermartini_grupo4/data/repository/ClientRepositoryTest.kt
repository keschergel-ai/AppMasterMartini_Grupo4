package com.example.appmastermartini_grupo4.data.repository

import com.example.appmastermartini_grupo4.data.local.ClientDao
import com.example.appmastermartini_grupo4.data.local.ClientEntity
import com.example.appmastermartini_grupo4.data.remote.MicroserviceApiService
import com.example.appmastermartini_grupo4.model.ClientModel
import io.mockk.*
import kotlinx.coroutines.flow.first
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
 * 1. Qué hace: Pruebas unitarias completas para [ClientRepository] utilizando JUnit 4 y MockK.
 *    Verifica la obtención de clientes, inserción, actualización, eliminación y la tolerancia a fallos
 *    cuando el microservicio Spring Boot lanza excepciones (resiliencia offline con Room DB).
 * 2. Conexiones: Mockea la capa de datos local [ClientDao] y la API remota [MicroserviceApiService].
 * 3. Rol en Testing: Garantiza la confiabilidad del patrón Repository como única fuente de verdad (Single Source of Truth).
 */
class ClientRepositoryTest {

    // Dependencias simuladas mediante MockK
    private lateinit var clientDao: ClientDao
    private lateinit var apiService: MicroserviceApiService

    // Instancia del repositorio bajo prueba
    private lateinit var repository: ClientRepository

    @Before
    fun setUp() {
        // Inicialización de mocks relajados para evitar llamadas reales a la base de datos o red
        clientDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        every { clientDao.getAllClients() } returns flowOf(emptyList())
        repository = ClientRepository(clientDao, apiService)
    }

    @Test
    fun `obtener todos los clientes emite modelos de dominio mapeados desde Room DB`() = runTest {
        // EXPLICACIÓN: Se mockea la lista de entidades de la base de datos Room.
        // SE VERIFICA: Que el mapa a modelos de dominio (ClientModel) mantenga los datos correctos (nombre y teléfono).
        val entityList = listOf(
            ClientEntity(
                id = 1,
                nombre = "Pastelería Delicias",
                telefono = "912345678",
                correo = "contacto@delicias.cl",
                direccion = "Av. Providencia 123",
                latitud = -33.4489,
                longitud = -70.6693,
                fotoUri = "file://foto.jpg"
            )
        )
        every { clientDao.getAllClients() } returns flowOf(entityList)
        val customRepo = ClientRepository(clientDao, apiService)

        val result = customRepo.allClients.first()

        assertEquals(1, result.size)
        assertEquals("Pastelería Delicias", result[0].nombre)
        assertEquals("912345678", result[0].telefono)
        assertEquals("contacto@delicias.cl", result[0].correo)
        assertEquals("Av. Providencia 123", result[0].direccion)
    }

    @Test
    fun `refrescar desde API remota inserta clientes en Room DB cuando la respuesta es exitosa`() = runTest {
        // EXPLICACIÓN: Se mockea una respuesta exitosa (HTTP 200) del microservicio con una lista de clientes.
        // SE VERIFICA: Que cada cliente recibido de la API remota sea guardado en Room DB mediante clientDao.insertClient.
        val remoteList = listOf(
            ClientModel(id = 10, nombre = "Panadería Italia", telefono = "987654321", correo = "italia@pan.cl", direccion = "Av. Italia 500")
        )
        coEvery { apiService.getClients() } returns Response.success(remoteList)

        repository.refreshClientsFromRemote()

        coVerify(exactly = 1) { clientDao.insertClient(any()) }
    }

    @Test
    fun `refrescar desde API remota maneja excepciones de red sin desplomar la aplicacion`() = runTest {
        // EXPLICACIÓN: Se simula una caída del servidor o falta de conexión (HTTP 10.0.2.2:8080 offline) lanzando una Exception.
        // SE VERIFICA: Que la excepción sea capturada de forma segura por el bloque try/catch y no crashee la app.
        coEvery { apiService.getClients() } throws RuntimeException("Servidor Spring Boot fuera de línea")

        repository.refreshClientsFromRemote()

        // Si llega aquí sin lanzar excepción, la prueba es exitosa (resiliencia comprobada)
        coVerify(exactly = 0) { clientDao.insertClient(any()) }
    }

    @Test
    fun `insertar cliente lo guarda localmente y llama al microservicio remoto`() = runTest {
        // EXPLICACIÓN: Se inserta un nuevo cliente. Se mockea el DAO y el endpoint POST del microservicio.
        // SE VERIFICA: Que se invoque la inserción local en Room y la llamada a la API remota, retornando Result.success.
        val client = ClientModel(
            id = 1,
            nombre = "Panadería Central",
            telefono = "987654321",
            correo = "central@pan.cl",
            direccion = "Calle Principal 456"
        )
        coEvery { clientDao.insertClient(any()) } returns 1L
        coEvery { apiService.createClient(any()) } returns Response.success(client)

        val result = repository.insertClient(client)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { clientDao.insertClient(any()) }
        coVerify(exactly = 1) { apiService.createClient(client) }
    }

    @Test
    fun `insertar cliente responde con exito local aunque el microservicio falle con excepcion`() = runTest {
        // EXPLICACIÓN: Se simula una falla de red (Exception) al intentar enviar el cliente al servidor Spring Boot.
        // SE VERIFICA: Que el cliente permanezca guardado en Room DB y la función devuelva Result.success (tolerancia a fallos).
        val client = ClientModel(id = 2, nombre = "Local Test", telefono = "911223344", correo = "test@local.cl", direccion = "Pasaje 1")
        coEvery { clientDao.insertClient(any()) } returns 2L
        coEvery { apiService.createClient(any()) } throws RuntimeException("Error de conexión con el servidor")

        val result = repository.insertClient(client)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { clientDao.insertClient(any()) }
    }

    @Test
    fun `actualizar cliente modifica la base de datos local y llama al microservicio`() = runTest {
        // EXPLICACIÓN: Se actualizan los datos de un cliente existente.
        // SE VERIFICA: Que se ejecute updateClient en el DAO local y en la API remota.
        val client = ClientModel(id = 5, nombre = "Pastelería Modificada", telefono = "955554444", correo = "mod@pan.cl", direccion = "Av. Chile 100")
        coEvery { clientDao.updateClient(any()) } just Runs
        coEvery { apiService.updateClient(5, client) } returns Response.success(client)

        val result = repository.updateClient(client)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { clientDao.updateClient(any()) }
        coVerify(exactly = 1) { apiService.updateClient(5, client) }
    }

    @Test
    fun `actualizar cliente responde con exito local aunque la API remota lance excepcion`() = runTest {
        // EXPLICACIÓN: Se simula que la API remota falla durante la actualización.
        // SE VERIFICA: Que la base de datos local Room sí se actualice y retorne Result.success.
        val client = ClientModel(id = 5, nombre = "Pastelería Offline", telefono = "955554444", correo = "off@pan.cl", direccion = "Av. Chile 100")
        coEvery { clientDao.updateClient(any()) } just Runs
        coEvery { apiService.updateClient(any(), any()) } throws RuntimeException("Timeout de red")

        val result = repository.updateClient(client)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { clientDao.updateClient(any()) }
    }

    @Test
    fun `eliminar cliente remueve el registro localmente y llama a la API remota`() = runTest {
        // EXPLICACIÓN: Se solicita eliminar un cliente por su identificador.
        // SE VERIFICA: Que se elimine de la base de datos Room (deleteClientById) y se envíe el DELETE a Retrofit.
        val clientId = 1
        coEvery { clientDao.deleteClientById(clientId) } just Runs
        coEvery { apiService.deleteClient(clientId) } returns Response.success(Unit)

        val result = repository.deleteClient(clientId)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { clientDao.deleteClientById(clientId) }
        coVerify(exactly = 1) { apiService.deleteClient(clientId) }
    }

    @Test
    fun `eliminar cliente mantiene exito local si la API remota esta fuera de linea`() = runTest {
        // EXPLICACIÓN: Se simula una caída de red al eliminar en el microservicio.
        // SE VERIFICA: Que la eliminación local en Room sea exitosa y la función retorne Result.success.
        val clientId = 2
        coEvery { clientDao.deleteClientById(clientId) } just Runs
        coEvery { apiService.deleteClient(clientId) } throws RuntimeException("Servidor no responde")

        val result = repository.deleteClient(clientId)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { clientDao.deleteClientById(clientId) }
    }
}
