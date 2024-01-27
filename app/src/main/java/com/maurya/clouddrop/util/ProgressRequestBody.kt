package com.maurya.clouddrop.util

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*


class ProgressRequestBody(
    private val delegate: RequestBody,
    private val callback: (Int) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    override fun contentLength(): Long {
        return delegate.contentLength()
    }

    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink,contentLength()) { bytesWritten, contentLength ->
            val progress = ((bytesWritten.toDouble() / contentLength) * 100).toInt()
            callback.invoke(progress)
        }

        val bufferedSink = countingSink.buffer()
        delegate.writeTo(bufferedSink)
        bufferedSink.flush()
    }
}
