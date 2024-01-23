package com.maurya.clouddrop.model

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class ProgressRequestBody(
    private val file: File,
    private val mediaType: MediaType?,
    private val progressCallback: ((progress: Float) -> Unit)?
) : RequestBody() {

    override fun contentType(): MediaType? = mediaType

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {
        val fileLength = contentLength()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val inSt = FileInputStream(file)
        var uploaded = 0L
        inSt.use {
            var read: Int = inSt.read(buffer)
            val handler = Handler(Looper.getMainLooper())
            while (read != -1) {
                progressCallback?.let {
                    uploaded += read
                    val progress = (uploaded.toDouble() / fileLength.toDouble()).toFloat()
                    handler.post { it(progress) }
                }
                sink.write(buffer, 0, read)
                read = inSt.read(buffer)
            }
        }
    }
}
