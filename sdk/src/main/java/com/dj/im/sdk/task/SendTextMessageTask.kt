package com.dj.im.sdk.task

import com.dj.im.sdk.Constant
import com.dj.im.sdk.convert.send.AbsSendMessageTask
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.proto.PrSendMessage
import java.util.*

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 发送文字消息的任务
 */
open class SendTextMessageTask : AbsSendMessageTask() {

    private lateinit var mMessage: ImMessage

    override fun sendMessage(message: Message): Message? {
        this.mMessage = message.getImMessage()
        // 开始发送
        startSend()
        return message
    }

    override fun onCmdId(): Int = Constant.CMD.SEND_MESSAGE

    override fun onReq2Buf(): ByteArray = PrSendMessage.SendMessageRequest.newBuilder()
        .setConversationId(mMessage.conversationId).setConversationType(mMessage.conversationType)
        .setFromId(mMessage.fromId).setToId(mMessage.toId).setType(mMessage.type).setData(mMessage.data)
        .setSummary(mMessage.summary)
        .build().toByteArray()

    override fun onBuf2Resp(buf: ByteArray?) {
        val response = PrResponseMessage.Response.parseFrom(buf)
        if (response.success) {
            val result = PrSendMessage.SendMessageResponse.parseFrom(response.data)
            mMessage.id = result.id
            mMessage.createTime = Date(result.createTime)
            // 还是发送中的状态，等kafka的回调
            mMessage.state = ImMessage.State.LOADING
        } else {
            // 发送失败
            mMessage.createTime = Date()
            mMessage.state = ImMessage.State.FAIL
        }
    }

    override fun onTaskEnd(errType: Int, errCode: Int) {
        if (errCode != 0) {
            // 如果有错误（一般是网络问题）,设置为发送失败
            mMessage.createTime = Date()
            mMessage.state = ImMessage.State.FAIL
        }
        // 保存到数据库中
        save(mMessage)
        // 通知更新
        notifyChangeState(mMessage.id, mMessage.state)
    }

}