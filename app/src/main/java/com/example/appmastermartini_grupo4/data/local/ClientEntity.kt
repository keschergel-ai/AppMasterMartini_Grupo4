package com.example.appmastermartini_grupo4.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appmastermartini_grupo4.model.ClientModel

/**
 * [ClientEntity]
 *
 * 1. Qué hace: Representa la tabla "clientes" en la base de datos local SQLite mediante Room.
 * 2. Conexiones: Es gestionada por [ClientDao] y mapeada hacia/desde [ClientModel] en el [ClientRepository].
 * 3. Rol en MVVM: Capa de Datos Local (Persistence Entity). Permite que la aplicación funcione offline con persistencia local.
 */
@Entity(tableName = "clientes")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val direccion: String,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val fotoUri: String = ""
) {
    /**
     * Convierte la entidad de Room a un modelo de dominio [ClientModel].
     */
    fun toDomain(): ClientModel {
        return ClientModel(
            id = id,
            nombre = nombre,
            telefono = telefono,
            correo = correo,
            direccion = direccion,
            latitud = latitud,
            longitud = longitud,
            fotoUri = fotoUri
        )
    }

    companion object {
        /**
         * Crea una [ClientEntity] a partir de un [ClientModel].
         */
        fun fromDomain(model: ClientModel): ClientEntity {
            return ClientEntity(
                id = model.id,
                nombre = model.nombre,
                telefono = model.telefono,
                correo = model.correo,
                direccion = model.direccion,
                latitud = model.latitud,
                longitud = model.longitud,
                fotoUri = model.fotoUri
            )
        }
    }
}
