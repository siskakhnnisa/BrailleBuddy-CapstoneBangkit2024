package com.example.brailleapp.retrofit

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

fun FilePart(filePath: String): MultipartBody.Part {
    val file = File(filePath)
    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(),file)
    return MultipartBody.Part.createFormData("image", file.name, requestFile)
}