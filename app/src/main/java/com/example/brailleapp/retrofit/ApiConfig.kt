package com.example.brailleapp.retrofit

import com.example.brailleapp.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = BuildConfig.BASE_URL
    private const val BASE_IMAGE_URL = BuildConfig.BASE_IMAGE_URL

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val apiImageService: ApiImageService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_IMAGE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiImageService::class.java)
    }
}