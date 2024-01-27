package com.maurya.clouddrop.api

import com.maurya.clouddrop.model.EmailRequest
import com.maurya.clouddrop.model.UploadResponse
import com.maurya.clouddrop.util.ProgressRequestBody
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LinksAPI {

    @Multipart
    @POST("files/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part,
                           @Part param: MultipartBody.Part): Response<UploadResponse>


    @POST("files/upload/send")
    suspend fun sendEmail(@Body emailRequest: EmailRequest): Response<EmailRequest>
}