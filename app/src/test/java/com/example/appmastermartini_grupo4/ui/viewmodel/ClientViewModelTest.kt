package com.example.appmastermartini_grupo4.ui.viewmodel

import com.example.appmastermartini_grupo4.data.repository.ClientRepository
import com.example.appmastermartini_grupo4.model.ClientModel
import com.example.appmastermartini_grupo4.rule.MainDispatcherRule
import com.example.appmastermartini_grupo4.ui.screens.LoginValidator
import com.example.appmastermartini_grupo4.ui.screens.filterClients
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * [ClientViewModelTest]
 *
 * 1. Qué hace: Pruebas unitarias integrales para el ViewModel de clientes ([ClientViewModel]) y las reglas de negocio de la UI.
 *    Cubre las validaciones estrictas del formulario (campos vacíos, nombre corto, teléfono corto, correo inválido),
 *    el reseteo del formulario, la gestión de GPS y foto, la eliminación de clientes, la validación de Login y el filtro de búsqueda.
 * 2. Conexiones: Utiliza [MainDispatcherRule] con `UnconfinedTestDispatcher` para corrutinas y mockea [ClientRepository] con MockK.
 * 3. Rol en Testing: Garantiza el cumplimiento de las reglas del formulario y la estabilidad de los estados reactivos.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ClientViewModelTest {

    // Regla para sustituir Dispatchers.Main por UnconfinedTestDispatcher en corrutinas
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mock del repositorio de datos
    private lateinit var repository: ClientRepository

    // Instancia del ViewModel bajo prueba
    private lateinit var viewModel: ClientViewModel

    @Before
    fun setUp() {
        // Inicialización de mocks y ViewModel antes de cada prueba
        repository = mockk(relaxed = true)
        every { repository.allClients } returns flowOf(emptyList())
        viewModel = ClientViewModel(repository)
    }

    @Test
    fun `validacion con 4 campos vacios establece mensajes de error en todos los campos`() {
        // EXPLICACIÓN: Se evalúa el comportamiento del formulario cuando los 4 campos obligatorios están en blanco.
        // SE VERIFICA: Que cada error individual se active con su respectivo mensaje en español.
        val isValid = viewModel.validateAllFields()

        assertFalse(isValid)
        assertEquals("El nombre del local es obligatorio *", viewModel.nombreError)
        assertEquals("El teléfono de contacto es obligatorio *", viewModel.telefonoError)
        assertEquals("El correo electrónico es obligatorio *", viewModel.correoError)
        assertEquals("La dirección del local es obligatoria *", viewModel.direccionError)
    }

    @Test
    fun `validacion de nombre con menos de 3 caracteres asigna error`() {
        // EXPLICACIÓN: Se ingresa un nombre demasiado corto ("Ab").
        // SE VERIFICA: Que se indique el requisito de al menos 3 caracteres.
        viewModel.onNombreChanged("Ab")

        assertEquals("Ab", viewModel.nombreInput)
        assertEquals("El nombre debe tener al menos 3 caracteres", viewModel.nombreError)
    }

    @Test
    fun `validacion de telefono con menos de 8 digitos asigna error`() {
        // EXPLICACIÓN: Se ingresa un teléfono con solo 5 dígitos.
        // SE VERIFICA: Que se indique el requisito de al menos 8 dígitos numéricos.
        viewModel.onTelefonoChanged("12345")

        assertEquals("12345", viewModel.telefonoInput)
        assertEquals("El teléfono debe contener al menos 8 dígitos numéricos", viewModel.telefonoError)
    }

    @Test
    fun `validacion de correo sin formato valido asigna error`() {
        // EXPLICACIÓN: Se ingresa un correo sin arroba ni dominio ("correopasteleria").
        // SE VERIFICA: Que se muestre el mensaje de formato inválido.
        viewModel.onCorreoChanged("correopasteleria")

        assertEquals("correopasteleria", viewModel.correoInput)
        assertEquals("Formato de correo inválido (ej: contacto@local.cl)", viewModel.correoError)
    }

    @Test
    fun `validacion de direccion corta asigna error`() {
        // EXPLICACIÓN: Se ingresa una dirección de menos de 5 caracteres ("Av.").
        // SE VERIFICA: Que se requiera una dirección de al menos 5 caracteres.
        viewModel.onDireccionChanged("Av.")

        assertEquals("Av.", viewModel.direccionInput)
        assertEquals("La dirección debe tener al menos 5 caracteres", viewModel.direccionError)
    }

    @Test
    fun `formulario con los 4 campos validos retorna true y limpia errores`() {
        // EXPLICACIÓN: Se completan los 4 campos con datos válidos.
        // SE VERIFICA: Que validateAllFields retorne true y los 4 errores sean nulos.
        viewModel.onNombreChanged("Pastelería San José")
        viewModel.onTelefonoChanged("912345678")
        viewModel.onCorreoChanged("contacto@sanjose.cl")
        viewModel.onDireccionChanged("Av. España 456")

        val isValid = viewModel.validateAllFields()

        assertTrue(isValid)
        assertNull(viewModel.nombreError)
        assertNull(viewModel.telefonoError)
        assertNull(viewModel.correoError)
        assertNull(viewModel.direccionError)
    }

    @Test
    fun `guardar cliente invoca al repositorio e invoca callback de exito cuando es valido`() = runTest {
        // EXPLICACIÓN: Se completan los datos válidos y se llama a saveClient.
        // SE VERIFICA: Que el repositorio inserte el cliente y se ejecute la lambda onSuccess.
        var successCalled = false
        viewModel.onNombreChanged("Panadería Italia")
        viewModel.onTelefonoChanged("987654321")
        viewModel.onCorreoChanged("italia@pan.cl")
        viewModel.onDireccionChanged("Av. Italia 1234")
        viewModel.setLocation(-33.4500, -70.6600)
        viewModel.setFotoUri("content://photo.jpg")

        coEvery { repository.insertClient(any()) } returns Result.success(Unit)

        viewModel.saveClient {
            successCalled = true
        }

        assertTrue(successCalled)
        coVerify(exactly = 1) { repository.insertClient(any()) }
    }

    @Test
    fun `guardar cliente no llama al repositorio si el formulario es invalido`() = runTest {
        // EXPLICACIÓN: Se intenta guardar con campos vacíos.
        // SE VERIFICA: Que el repositorio NO sea invocado y el callback de éxito no se ejecute.
        var successCalled = false

        viewModel.saveClient {
            successCalled = true
        }

        assertFalse(successCalled)
        coVerify(exactly = 0) { repository.insertClient(any()) }
    }

    @Test
    fun `eliminar cliente llama al repositorio y ejecuta callback de exito`() = runTest {
        // EXPLICACIÓN: Se solicita eliminar el cliente con ID 10.
        // SE VERIFICA: Que el repositorio elimine dicho ID y se confirme la eliminación.
        var deleted = false
        coEvery { repository.deleteClient(10) } returns Result.success(Unit)

        viewModel.deleteClient(10) {
            deleted = true
        }

        assertTrue(deleted)
        coVerify(exactly = 1) { repository.deleteClient(10) }
    }

    @Test
    fun `resetForm limpia todas las entradas y errores del formulario`() {
        // EXPLICACIÓN: Se llenan datos en el formulario y luego se invoca resetForm.
        // SE VERIFICA: Que los campos de texto vuelvan a estar vacíos y las coordenadas a sus valores por defecto.
        viewModel.onNombreChanged("Local Test")
        viewModel.setLocation(-30.0, -71.0)
        viewModel.setFotoUri("content://foto.png")

        viewModel.resetForm()

        assertEquals("", viewModel.nombreInput)
        assertEquals("", viewModel.telefonoInput)
        assertEquals("", viewModel.correoInput)
        assertEquals("", viewModel.direccionInput)
        assertEquals(-33.4489, viewModel.latitudInput, 0.0001)
        assertEquals(-70.6693, viewModel.longitudInput, 0.0001)
        assertEquals("", viewModel.fotoUriInput)
        assertNull(viewModel.nombreError)
    }

    @Test
    fun `validacion de Login con correo o clave invalida rechaza la autenticacion`() {
        // EXPLICACIÓN: Se prueban credenciales inválidas en el validador de Login.
        // SE VERIFICA: Que correos sin arroba/dominio o contraseñas de menos de 4 caracteres sean rechazadas.
        assertFalse(LoginValidator.isValid("", ""))
        assertFalse(LoginValidator.isValid("correoInvalido", "1234"))
        assertFalse(LoginValidator.isValid("kevin@martini.cl", "123"))
        assertEquals("Ingrese un correo institucional válido (ej: kevin@martini.cl)", LoginValidator.validateEmail("invalid"))
        assertEquals("La contraseña debe tener al menos 4 caracteres", LoginValidator.validatePassword("123"))
    }

    @Test
    fun `validacion de Login con credenciales validas aprueba el ingreso`() {
        // EXPLICACIÓN: Se prueban credenciales de Login correctas (correo bien formado y clave >= 4 caracteres).
        // SE VERIFICA: Que LoginValidator retorne verdadedo y sin errores.
        assertTrue(LoginValidator.isValid("kevin@martini.cl", "Martini123!"))
        assertNull(LoginValidator.validateEmail("kevin@martini.cl"))
        assertNull(LoginValidator.validatePassword("Martini123!"))
    }

    @Test
    fun `filtro de busqueda filtra clientes por nombre o direccion correctamente`() {
        // EXPLICACIÓN: Se filtran clientes registrados utilizando una cadena de búsqueda.
        // SE VERIFICA: Que la búsqueda por nombre o por dirección filtre la lista ignorando mayúsculas/minúsculas.
        val clients = listOf(
            ClientModel(id = 1, nombre = "Pastelería Dulce", telefono = "911", correo = "d@d.cl", direccion = "Av. Italia 100"),
            ClientModel(id = 2, nombre = "Panadería Don Juan", telefono = "922", correo = "j@j.cl", direccion = "Calle Matta 200")
        )

        val resultByName = filterClients(clients, "dulce")
        assertEquals(1, resultByName.size)
        assertEquals("Pastelería Dulce", resultByName[0].nombre)

        val resultByAddress = filterClients(clients, "matta")
        assertEquals(1, resultByAddress.size)
        assertEquals("Panadería Don Juan", resultByAddress[0].nombre)

        val resultEmptyQuery = filterClients(clients, "")
        assertEquals(2, resultEmptyQuery.size)
    }
}
