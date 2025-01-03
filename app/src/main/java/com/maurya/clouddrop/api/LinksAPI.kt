package com.maurya.clouddrop.api

import com.maurya.clouddrop.database.EmailRequest
import com.maurya.clouddrop.database.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LinksAPI {

    @Multipart
    @POST("files/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<UploadResponse>


    @POST("files/upload/send")
    suspend fun sendEmail(@Body emailRequest: EmailRequest): Response<EmailRequest>
}