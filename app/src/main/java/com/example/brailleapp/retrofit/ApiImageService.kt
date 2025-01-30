package com.example.brailleapp.retrofit

import retrofit2.http.GET

interface ApiImageService {
    @GET("images")
    suspend fun getImages(): ImageResponse
}