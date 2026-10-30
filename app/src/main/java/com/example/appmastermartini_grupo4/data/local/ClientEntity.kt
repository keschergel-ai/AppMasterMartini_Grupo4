package com.example.appmastermartini_grupo4.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Representa la tabla "clientes" en la base de datos SQLite local
@Entity(tableName = "clientes")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,               // ID único autoincrementable
    val nombre: String,            // Nombre del cliente/local
    val telefono: String = ""       // Teléfono o nota de contacto
)