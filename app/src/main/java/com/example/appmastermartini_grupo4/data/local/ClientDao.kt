package com.example.appmastermartini_grupo4.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Métodos para guardar y consultar en la base de datos
@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)

    @Query("SELECT * FROM clientes ORDER BY id DESC")
    fun getAllClients(): Flow<List<ClientEntity>>
}