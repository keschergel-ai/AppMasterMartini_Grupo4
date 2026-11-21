package com.example.appmastermartini_grupo4.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * [AppDatabase]
 *
 * 1. Qué hace: Configura y provee la instancia Singleton de la base de datos Room, incluyendo [.fallbackToDestructiveMigration()] para evitar crashes por cambios de esquema.
 * 2. Conexiones: Expone [ClientDao] hacia [ClientRepository].
 * 3. Rol en MVVM: Capa de Datos Local (Database Singleton).
 */
@Database(entities = [ClientEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "master_martini_database"
                )
                    .fallbackToDestructiveMigration() // Evita cierre por cambios de versión en la BD
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
