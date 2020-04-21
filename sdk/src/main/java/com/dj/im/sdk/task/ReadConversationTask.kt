package com.dj.im.sdk.task

import com.dj.im.sdk.Constant
import com.dj.im.sdk.ITask
import com.dj.im.sdk.proto.PrReadConversation


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 已读会话消息任务
 */
internal class ReadConversationTask(private val mConversationId: String) : ITask.Stub() {

    override fun onCmdId(): Int = Constant.CMD.READ_CONVERSATION

    override fun onReq2Buf(): ByteArray =
        PrReadConversation.ReadConversationRequest.newBuilder().setConversationId(mConversationId)
            .build().toByteArray()

    override fun onBuf2Resp(buf: ByteArray?) {

    }

    override fun onTaskEnd(errType: Int, errCode: Int) {
    }

}
