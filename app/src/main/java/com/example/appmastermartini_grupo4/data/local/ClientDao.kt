package com.example.appmastermartini_grupo4.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * [ClientDao]
 *
 * 1. Qué hace: Proporciona las operaciones CRUD SQL para acceder a la tabla "clientes" en Room DB.
 * 2. Conexiones: Invocado por [AppDatabase] y [ClientRepository].
 * 3. Rol en MVVM: Capa de Datos Local (DAO), permitiendo consultas reactivas mediante Coroutines [Flow].
 */
@Dao
interface ClientDao {

    @Query("SELECT * FROM clientes ORDER BY id DESC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clientes WHERE id = :clientId")
    suspend fun getClientById(clientId: Int): ClientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Query("DELETE FROM clientes WHERE id = :clientId")
    suspend fun deleteClientById(clientId: Int)

    @Delete
    suspend fun deleteClient(client: ClientEntity)
}
