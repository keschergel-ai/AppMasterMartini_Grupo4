package com.example.appmastermartini_grupo4.model

/**
 * [ClientModel]
 *
 * 1. Qué hace: Modelo de datos de dominio que representa un cliente gastronómico (panadería, pastelería o local).
 * 2. Conexiones: Se utiliza en el [ClientRepository], [ClientViewModel] y en las pantallas de la UI ([HomeScreen], [FormScreen], [DetailScreen]).
 * 3. Rol en MVVM: Actúa como la entidad de Modelo pura (Data Transfer Object / Dominio), desacoplando la base de datos local (Room) y la API remota (Retrofit) de la interfaz de usuario.
 */
data class ClientModel(
    val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val direccion: String,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val fotoUri: String = ""
)
