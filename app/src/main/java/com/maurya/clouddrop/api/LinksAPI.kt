package com.maurya.clouddrop.api

import com.maurya.clouddrop.module.DownloadLinkResponse
import com.maurya.clouddrop.module.EmailResponse
import com.maurya.clouddrop.module.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.UUID

interface LinksAPI {

    @Multipart
    @POST("files/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<UploadResponse>

    @POST("files/uuid")
    suspend fun generateDownloadLink(@Path("uuid") uuid: String): Response<DownloadLinkResponse>

    @POST("files/upload/send")
    suspend fun sendEmail(@Body emailRequest: String): Response<EmailResponse>


}