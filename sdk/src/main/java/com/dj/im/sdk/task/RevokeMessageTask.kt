package com.dj.im.sdk.task

import com.dj.im.sdk.Constant
import com.dj.im.sdk.ITask
import com.dj.im.sdk.proto.PrRevokeMessage

/**
 * Create by ChenLei on 2021/1/11
 * Describe: 撤回消息任务
 */
internal class RevokeMessageTask(private val mMessageId: Long) : ITask.Stub() {

    override fun onCmdId(): Int = Constant.CMD.REVOKE_MESSAGE

    override fun onReq2Buf(): ByteArray = PrRevokeMessage.RevokeMessageRequest.newBuilder()
        .setMessageId(mMessageId).build().toByteArray()

    override fun onBuf2Resp(buf: ByteArray?) {
    }

    override fun onTaskEnd(errType: Int, errCode: Int) {
    }
}