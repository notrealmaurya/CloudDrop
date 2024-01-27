package com.maurya.clouddrop.repository

import android.util.Log
import com.maurya.clouddrop.api.LinksAPI
import com.maurya.clouddrop.model.EmailRequest
import com.maurya.clouddrop.util.ProgressRequestBody
import com.maurya.clouddrop.util.extractUuidFromLink
import okhttp3.MultipartBody
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
        val progressRequestBody = ProgressRequestBody(file) { progress ->
            callback.onProgressUpdate(progress)
        }

        val filePart = MultipartBody.Part.createFormData("myfile", file.name, progressRequestBody)

        val response = linksAPI.uploadFile(filePart)

        val link = response.body()?.file.orEmpty()
        val uuid = extractUuidFromLink(link)
        generateDownloadLink(uuid, callback)
        Log.d("MyLinkRepo", link)
        Log.d("MyLinkRepo", uuid)
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
