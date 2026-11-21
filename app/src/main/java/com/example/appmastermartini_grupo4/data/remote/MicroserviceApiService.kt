package com.example.appmastermartini_grupo4.data.remote

import com.example.appmastermartini_grupo4.model.ClientModel
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * [MicroserviceApiService]
 *
 * 1. Qué hace: Define los endpoints REST (GET, POST, PUT, DELETE) para comunicarse con el microservicio Spring Boot.
 *    NOTA DE CONFIGURACIÓN: Apunta a `http://10.0.2.2:8080/` (IP especial del emulador Android para localhost).
 *    Si se conecta a un dispositivo físico, cambiar por la IP local de la PC (ej: `http://192.168.1.50:8080/`) o usar un túnel ngrok.
 * 2. Conexiones: Invocado por [ClientRepository].
 * 3. Rol en MVVM: Capa de Datos Remota (Retrofit API Service).
 */
interface MicroserviceApiService {

    @GET("api/clientes")
    suspend fun getClients(): Response<List<ClientModel>>

    @GET("api/clientes/{id}")
    suspend fun getClientById(@Path("id") id: Int): Response<ClientModel>

    @POST("api/clientes")
    suspend fun createClient(@Body client: ClientModel): Response<ClientModel>

    @PUT("api/clientes/{id}")
    suspend fun updateClient(@Path("id") id: Int, @Body client: ClientModel): Response<ClientModel>

    @DELETE("api/clientes/{id}")
    suspend fun deleteClient(@Path("id") id: Int): Response<Unit>

    companion object {
        // URL base para emulador Android apuntando a Spring Boot en localhost:8080
        private const val BASE_URL = "http://10.0.2.2:8080/"

        fun create(): MicroserviceApiService {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MicroserviceApiService::class.java)
        }
    }
}
