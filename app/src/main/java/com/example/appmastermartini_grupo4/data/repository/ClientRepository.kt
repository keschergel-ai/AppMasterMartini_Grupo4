package com.example.appmastermartini_grupo4.data.repository

import com.example.appmastermartini_grupo4.data.local.ClientDao
import com.example.appmastermartini_grupo4.data.local.ClientEntity
import com.example.appmastermartini_grupo4.data.remote.MicroserviceApiService
import com.example.appmastermartini_grupo4.model.ClientModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [ClientRepository]
 *
 * 1. Qué hace: Actúa como única fuente de verdad (Single Source of Truth), coordinando la persistencia local (Room DB via [ClientDao]) y el microservicio remoto ([MicroserviceApiService]).
 *    Incluye manejo seguro de excepciones (`try/catch`) para que el CRUD funcione de manera fluida en la UI incluso si el servidor Spring Boot está apagado.
 * 2. Conexiones: Conecta [ClientDao] y [MicroserviceApiService] con el [ClientViewModel].
 * 3. Rol en MVVM: Capa de Repositorio (Repository Pattern).
 */
class ClientRepository(
    private val clientDao: ClientDao,
    private val apiService: MicroserviceApiService
) {

    /**
     * Obtiene todos los clientes en flujo reactivo desde Room DB local.
     */
    val allClients: Flow<List<ClientModel>> = clientDao.getAllClients().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Sincroniza datos remotos con la base de datos local (con manejo seguro ante caídas del servidor).
     */
    suspend fun refreshClientsFromRemote() {
        try {
            val response = apiService.getClients()
            if (response.isSuccessful) {
                response.body()?.let { remoteList ->
                    // Guardar en Room como caché local
                    for (client in remoteList) {
                        clientDao.insertClient(ClientEntity.fromDomain(client))
                    }
                }
            }
        } catch (e: Exception) {
            // El servidor puede estar offline; la app continúa operando con Room DB local sin crashear.
            e.printStackTrace()
        }
    }

    /**
     * Inserta un nuevo cliente de forma local y remota (con respaldo).
     */
    suspend fun insertClient(client: ClientModel): Result<Unit> {
        return try {
            // Guardar primero en Room DB local para inmediatez en UI
            clientDao.insertClient(ClientEntity.fromDomain(client))

            // Intentar enviar al microservicio Spring Boot
            try {
                val response = apiService.createClient(client)
                if (!response.isSuccessful) {
                    // Falló el servidor pero quedó persistido localmente
                }
            } catch (netEx: Exception) {
                netEx.printStackTrace()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza un cliente existente.
     */
    suspend fun updateClient(client: ClientModel): Result<Unit> {
        return try {
            clientDao.updateClient(ClientEntity.fromDomain(client))
            try {
                apiService.updateClient(client.id, client)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un cliente por ID local y remotamente.
     */
    suspend fun deleteClient(clientId: Int): Result<Unit> {
        return try {
            clientDao.deleteClientById(clientId)
            try {
                apiService.deleteClient(clientId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
