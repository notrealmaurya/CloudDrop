package com.maurya.clouddrop.repository

import com.maurya.clouddrop.api.LinksAPI
import com.maurya.clouddrop.fragments.HomeFragment
import com.maurya.clouddrop.model.EmailRequest
import com.maurya.clouddrop.util.extractUuidFromLink
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


class LinkRepository @Inject constructor(
    private var linksAPI: LinksAPI
) {


    suspend fun uploadFile(file: File) {
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("myfile", file.name, requestFile)
        val response = linksAPI.uploadFile(body)
        val link = response.body()?.file.orEmpty()
        val uuid = extractUuidFromLink(link)
        generateDownloadLink(uuid)
    }


    private fun generateDownloadLink(uuid: String) {
        val text = "https://fileshare-expressapi.onrender.com/files/$uuid"
        val homeFragment = HomeFragment()
        homeFragment.updateText(text)


    }


    suspend fun sendEmail(emailFrom: String, emailTo: String, fileId: String): String {
        val emailRequest = EmailRequest(emailFrom, emailTo, fileId)
        val response = linksAPI.sendEmail(emailRequest)
        return response.body()?.uuid.orEmpty()
    }


}
