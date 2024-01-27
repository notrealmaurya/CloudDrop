package com.maurya.clouddrop.util

import okio.Buffer
import okio.ForwardingSink
import okio.Sink

class CountingSink(
    delegate: Sink,
    private val contentLength: Long,
    private val callback: (Long, Long) -> Unit
) : ForwardingSink(delegate) {

    private var bytesWritten = 0L

    override fun write(source: Buffer, byteCount: Long) {
        super.write(source, byteCount)
        bytesWritten += byteCount
        callback.invoke(bytesWritten, contentLength)
    }
}
