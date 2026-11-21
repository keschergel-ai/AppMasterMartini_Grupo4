package com.example.appmastermartini_grupo4.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appmastermartini_grupo4.data.local.AppDatabase
import com.example.appmastermartini_grupo4.data.remote.MicroserviceApiService
import com.example.appmastermartini_grupo4.data.repository.ClientRepository
import com.example.appmastermartini_grupo4.model.ClientModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * [ClientViewModel]
 *
 * 1. Qué hace: Gestiona el estado de la interfaz de usuario para el CRUD de clientes, validaciones de formularios, fotos y ubicación GPS.
 * 2. Conexiones: Conecta [ClientRepository] con [HomeScreen], [FormScreen] y [DetailScreen].
 * 3. Rol en MVVM: Capa ViewModel (Mantiene el estado reactivo y procesa la lógica de negocio de la UI).
 */
class ClientViewModel(
    private val repository: ClientRepository
) : ViewModel() {

    init {
        // Carga inicial o sincronización remota protegida con try/catch contra fallos de servidor (HTTP 10.0.2.2:8080 offline)
        viewModelScope.launch {
            try {
                repository.refreshClientsFromRemote()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val database = AppDatabase.getDatabase(context.applicationContext)
            val microserviceApi = MicroserviceApiService.create()
            val repository = ClientRepository(database.clientDao(), microserviceApi)
            return ClientViewModel(repository) as T
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = Factory(context)
    }

    // Lista reactiva de clientes expuesta como StateFlow a la UI
    val clients: StateFlow<List<ClientModel>> = repository.allClients
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estados del Formulario
    var nombreInput by mutableStateOf("")
        private set
    var telefonoInput by mutableStateOf("")
        private set
    var correoInput by mutableStateOf("")
        private set
    var direccionInput by mutableStateOf("")
        private set
    var latitudInput by mutableStateOf(-33.4489)
        private set
    var longitudInput by mutableStateOf(-70.6693)
        private set
    var fotoUriInput by mutableStateOf("")
        private set

    // Errores de validación visual estricta de los 4 campos obligatorios
    var nombreError by mutableStateOf<String?>(null)
        private set
    var telefonoError by mutableStateOf<String?>(null)
        private set
    var correoError by mutableStateOf<String?>(null)
        private set
    var direccionError by mutableStateOf<String?>(null)
        private set

    fun onNombreChanged(value: String) {
        nombreInput = value
        nombreError = when {
            value.isBlank() -> "El nombre del local es obligatorio *"
            value.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }
    }

    fun onTelefonoChanged(value: String) {
        telefonoInput = value
        val digitsOnly = value.filter { it.isDigit() }
        telefonoError = when {
            value.isBlank() -> "El teléfono de contacto es obligatorio *"
            digitsOnly.length < 8 -> "El teléfono debe contener al menos 8 dígitos numéricos"
            else -> null
        }
    }

    fun onCorreoChanged(value: String) {
        correoInput = value
        correoError = when {
            value.isBlank() -> "El correo electrónico es obligatorio *"
            !value.contains("@") || !value.contains(".") -> "Formato de correo inválido (ej: contacto@local.cl)"
            else -> null
        }
    }

    fun onDireccionChanged(value: String) {
        direccionInput = value
        direccionError = when {
            value.isBlank() -> "La dirección del local es obligatoria *"
            value.trim().length < 5 -> "La dirección debe tener al menos 5 caracteres"
            else -> null
        }
    }

    fun setLocation(lat: Double, lon: Double) {
        latitudInput = lat
        longitudInput = lon
    }

    fun setFotoUri(uri: String) {
        fotoUriInput = uri
    }

    fun validateAllFields(): Boolean {
        onNombreChanged(nombreInput)
        onTelefonoChanged(telefonoInput)
        onCorreoChanged(correoInput)
        onDireccionChanged(direccionInput)

        return nombreError == null && telefonoError == null && correoError == null && direccionError == null
    }

    fun resetForm() {
        nombreInput = ""
        telefonoInput = ""
        correoInput = ""
        direccionInput = ""
        latitudInput = -33.4489
        longitudInput = -70.6693
        fotoUriInput = ""
        nombreError = null
        telefonoError = null
        correoError = null
        direccionError = null
    }

    fun saveClient(onSuccess: () -> Unit) {
        if (!validateAllFields()) {
            return
        }

        viewModelScope.launch {
            val newClient = ClientModel(
                nombre = nombreInput.trim(),
                telefono = telefonoInput.trim(),
                correo = correoInput.trim(),
                direccion = direccionInput.trim(),
                latitud = latitudInput,
                longitud = longitudInput,
                fotoUri = fotoUriInput
            )
            val result = repository.insertClient(newClient)
            if (result.isSuccess) {
                resetForm()
                onSuccess()
            }
        }
    }

    fun deleteClient(clientId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteClient(clientId)
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}
