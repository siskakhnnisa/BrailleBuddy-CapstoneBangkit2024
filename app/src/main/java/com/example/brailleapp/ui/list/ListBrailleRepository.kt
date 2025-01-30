package com.example.brailleapp.ui.list

import com.example.brailleapp.retrofit.ApiImageService
import com.example.brailleapp.retrofit.ImageResponse

class ListBrailleRepository(private val apiImageService: ApiImageService) {
    suspend fun getImages(): ImageResponse {
        return apiImageService.getImages()
    }
}