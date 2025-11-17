package com.example.detectify.api



import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()
    }

    val visionService: VisionService by lazy {
        Retrofit.Builder()
            .baseUrl("https://vision.googleapis.com/") // Vision REST base
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VisionService::class.java)
    }
}
