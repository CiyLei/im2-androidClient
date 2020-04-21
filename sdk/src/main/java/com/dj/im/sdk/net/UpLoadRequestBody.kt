package com.dj.im.sdk.net

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*


/**
 * Create by ChenLei on 2020/4/21
 * Describe: 上传文件进度监听请求类
 */
class UpLoadRequestBody(
    private val mRequestBody: RequestBody,
    private val callback: ((Float) -> Unit)?
) : RequestBody() {

    override fun contentType(): MediaType? = mRequestBody.contentType()

    override fun contentLength(): Long = mRequestBody.contentLength()

    override fun writeTo(sink: BufferedSink) {
        val sink = CountingSink(sink, contentLength(), callback)
        val buffer = Okio.buffer(sink)
        //写入
        mRequestBody.writeTo(buffer)
        //必须调用flush，否则最后一部分数据可能不会被写入
        buffer.flush()
    }

    class CountingSink(delegate: Sink?, val contentLength: Long, val callback: ((Float) -> Unit)?) :
        ForwardingSink(delegate) {
        private var bytesWritten: Long = 0

        override fun write(source: Buffer?, byteCount: Long) {
            super.write(source, byteCount)
            bytesWritten += byteCount
            callback?.invoke(bytesWritten.toFloat() / contentLength.toFloat())
        }
    }
}