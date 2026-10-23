package com.example.appmastermartini_grupo4.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var nombreCliente by mutableStateOf("")
        private set
    var correoCliente by mutableStateOf("")
        private set
    var correoError by mutableStateOf<String?>(null)
        private set

    fun onNombreChange(value: String) {
        nombreCliente = value
    }

    fun onCorreoChange(value: String) {
        correoCliente = value
        correoError = if (value.contains("@") && value.contains(".")) {
            null
        } else {
            "Correo electrónico inválido"
        }
    }

    fun isFormValid(): Boolean {
        return nombreCliente.isNotBlank() && correoCliente.isNotBlank() && correoError == null
    }
}