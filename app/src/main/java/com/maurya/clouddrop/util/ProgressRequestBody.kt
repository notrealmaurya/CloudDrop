package com.maurya.clouddrop.util

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.*
import java.io.File


class ProgressRequestBody(
    private val file: File,
    private val callback: (Int) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? {
        return "multipart/form-data".toMediaTypeOrNull()
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink, contentLength()) { bytesWritten, contentLength ->
            val progress = ((bytesWritten.toDouble() / contentLength) * 100).toInt()
            callback.invoke(progress)
        }

        val bufferedSink = countingSink.buffer()
        bufferedSink.writeAll(file.source())
        bufferedSink.flush()
    }
}
