package com.example.brailleapp.retrofit

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("data")
    val dataImage: List<ImageItem> = listOf()
)

data class ImageItem(
    val id: String,
    @SerializedName("name") val imageName: String,
    @SerializedName("imageUrl") val imgPhoto: String)