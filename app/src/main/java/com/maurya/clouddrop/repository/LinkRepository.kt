package com.maurya.clouddrop.repository

import com.maurya.clouddrop.api.LinksAPI
import com.maurya.clouddrop.model.EmailRequest
import com.maurya.clouddrop.util.ProgressRequestBody
import com.maurya.clouddrop.util.extractUuidFromLink
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


class LinkRepository @Inject constructor(
    private var linksAPI: LinksAPI
) {
    interface UploadCallback {
        fun onProgressUpdate(progress: Int)
        fun onUploadComplete(downloadLink: String)
    }

    suspend fun uploadFile(file: File, callback: UploadCallback) {
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("myfile", file.name, requestFile)

        val progressRequestBody = ProgressRequestBody(requestFile) { progress ->
            callback.onProgressUpdate(progress)
        }

        val progressParam = MultipartBody.Part.createFormData("progress", "paramValue", progressRequestBody)

        val response = linksAPI.uploadFile(body, progressParam)

        val link = response.body()?.file.orEmpty()
        val uuid = extractUuidFromLink(link)
        generateDownloadLink(uuid, callback)
    }


    private fun generateDownloadLink(uuid: String, callback: UploadCallback) {
        val text = "https://fileshare-expressapi.onrender.com/files/$uuid"
        callback.onUploadComplete(text)
    }

    suspend fun sendEmail(emailFrom: String, emailTo: String, fileId: String): String {
        val emailRequest = EmailRequest(emailFrom, emailTo, fileId)
        val response = linksAPI.sendEmail(emailRequest)
        return response.body()?.uuid.orEmpty()
    }
}
