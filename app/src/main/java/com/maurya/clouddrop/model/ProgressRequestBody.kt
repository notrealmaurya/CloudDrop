package com.maurya.clouddrop.model

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.buffer
import okio.source
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ProgressRequestBody(
    private val file: File,
    private val progressListener: ProgressListener
) : RequestBody() {


    override fun contentType(): MediaType? {
        // Set the media type for your file
        return "multipart/form-data".toMediaTypeOrNull()
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val source = file.source()
        val buffer = Buffer()
        var totalBytesRead: Long = 0

        while (true) {
            val bytesRead = source.read(buffer, SEGMENT_SIZE)
            if (bytesRead == -1L) break
            sink.write(buffer, bytesRead)

            totalBytesRead += bytesRead
            progressListener.onProgress(totalBytesRead, file.length())
        }

        source.close()
    }

    companion object {
        private const val SEGMENT_SIZE = 2048L
    }
}

interface ProgressListener {
    fun onProgress(bytesRead: Long, contentLength: Long)
}
