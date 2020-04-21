package com.dj.im.sdk.task

import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.FileMessage
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.net.RetrofitManager
import com.dj.im.sdk.net.UpLoadRequestBody
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import com.dj.im.sdk.utils.RxUtil.o
import okhttp3.MultipartBody


/**
 * Create by ChenLei on 2020/4/21
 * Describe: 发送文件的任务
 */
class SendFileMessageTask : SendTextMessageTask() {

    private lateinit var mFileMessage: FileMessage
    // 临时保存的本地文件路径
    private var mTmpLocalPath = ""

    override fun sendMessage(message: Message): Message? {
        // 校验是不是发送文件消息
        if (message is FileMessage) {
            mFileMessage = message
            // 开始上传
            startUpload()
            return message
        }
        return null
    }

    override fun getMessage(): Message = mFileMessage

    /**
     * 开始上传文件
     */
    private fun startUpload() {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val rb: RequestBody =
            RequestBody.create(
                MediaType.parse("form-data"),
                File(mFileMessage.fileEntity.localPath)
            )
        val uploadRequest = UpLoadRequestBody(rb) {
            // 更新上传进度
            mFileMessage.uploadProgress = it
            // 通知更新
            notifyChangeState()
        }
        val request =
            builder.addFormDataPart("file", mFileMessage.fileEntity.fileName, uploadRequest).build()
        val d = RetrofitManager.instance.apiStore.upload(request).o().subscribe({
            if (it.success) {
                // 上传成功
                mFileMessage.fileEntity.netResId = it.data
                // 本地的路径不需要发送给对方，先保存起来，在保存到数据库之前还原
                mTmpLocalPath = mFileMessage.fileEntity.localPath
                mFileMessage.fileEntity.localPath = ""
                // 更新data数据
                mFileMessage.updateData()
                // 开始发送消息
                startSend()
            } else {
                // 上传失败
                mFileMessage.imMessage.state = ImMessage.State.FAIL
                notifyChangeState()
                mFileMessage.save()
            }
        }, {
            // 上传失败
            mFileMessage.imMessage.state = ImMessage.State.FAIL
            notifyChangeState()
            mFileMessage.save()
        })
    }

    /**
     * 通知更新进度
     */
    private fun notifyChangeState() {
        notifyChangeState(mFileMessage.imMessage.id, mFileMessage.imMessage.state)
    }

    override fun onTaskEnd(errType: Int, errCode: Int) {
        // 还原本地文件路径
        mFileMessage.fileEntity.localPath = mTmpLocalPath
        mFileMessage.updateData()
        super.onTaskEnd(errType, errCode)
    }
}