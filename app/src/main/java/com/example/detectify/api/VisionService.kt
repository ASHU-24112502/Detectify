package com.example.detectify.api



import com.example.detectify.model.VisionResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface VisionService {
    @POST("v1/images:annotate")
    suspend fun annotate(
        @Query("key") key: String,
        @Body body: RequestBody
    ): Response<VisionResponse>
}
