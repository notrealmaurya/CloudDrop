package com.maurya.clouddrop.repository

import com.maurya.clouddrop.api.LinksAPI
import com.maurya.clouddrop.fragments.HomeFragment
import com.maurya.clouddrop.model.EmailRequest
import com.maurya.clouddrop.model.ProgressRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class LinkRepository @Inject constructor(
    private var linksAPI: LinksAPI
) {


    suspend fun uploadFile(file: File, progressCallback: (Int) -> Unit): String {
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        val body = MultipartBody.Part.createFormData("myfile", file.name, requestFile)

        val response = linksAPI.uploadFile(body)
        val link = response.body()?.file.orEmpty()

        val uuid = extractUuidFromLink(link)

        return generateDownloadLink(uuid)
    }


    private fun extractUuidFromLink(link: String): String {
        val parts = link.split("/")
        return parts.last()
    }

    private fun generateDownloadLink(uuid: String): String {
        val text = "https://fileshare-expressapi.onrender.com/files/$uuid"
        HomeFragment.fragmentHomeBinding.downloadLinkHomeFragment.text = text

        return text
    }


    suspend fun sendEmail(emailFrom: String, emailTo: String, fileId: String): String {
        val emailRequest = EmailRequest(emailFrom, emailTo, fileId)
        val response = linksAPI.sendEmail(emailRequest)
        return response.body()?.uuid.orEmpty()
    }


}
