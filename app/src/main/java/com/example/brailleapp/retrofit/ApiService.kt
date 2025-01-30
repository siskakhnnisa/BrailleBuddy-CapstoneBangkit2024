package com.example.brailleapp.retrofit

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("predict")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): PredictionResponse
}