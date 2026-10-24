package com.noctume.security.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class PublicacionDemo(val id: Int, val title: String, val body: String)

interface ServicioContenidoDemo {
    @GET("posts?_limit=5")
    suspend fun publicaciones(): List<PublicacionDemo>
}

object ClienteContenidoDemo {
    val servicio: ServicioContenidoDemo by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServicioContenidoDemo::class.java)
    }
}
