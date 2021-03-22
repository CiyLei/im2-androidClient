package com.dj.im.sdk.task

import com.dj.im.sdk.Constant
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.convert.send.AbsSendMessageTask
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.proto.PrSendMessage
import com.dj.im.sdk.service.ServiceManager

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 发送文字消息的任务
 */
open class SendTextMessageTask : AbsSendMessageTask() {

    private lateinit var mMessage: Message

    override fun matchTask(message: Message): AbsSendMessageTask? {
        this.mMessage = message
        return this
    }

    override fun getMessage(): Message = mMessage

    override fun onCmdId(): Int = Constant.CMD.SEND_MESSAGE

    override fun onReq2Buf(): ByteArray = PrSendMessage.SendMessageRequest.newBuilder()
        .setConversationKey(getMessage().imMessage.conversationKey)
        .setConversationType(getMessage().imMessage.conversationType)
        .setFromUserName(getMessage().imMessage.fromUserName)
        .setToUserName(getMessage().imMessage.toUserName)
        .setType(getMessage().imMessage.type)
        .setData(getMessage().imMessage.data)
        .setSummary(getMessage().imMessage.summary)
        .build().toByteArray()

    override fun onBuf2Resp(buf: ByteArray?) {
        val response = PrResponseMessage.Response.parseFrom(buf)
        if (response.success) {
            val result = PrSendMessage.SendMessageResponse.parseFrom(response.data)
            notifyChangeMessageId(getMessage().imMessage.id, result.id)
            getMessage().imMessage.id = result.id
            getMessage().imMessage.createTime = result.createTime
            // 还是发送中的状态，等kafka的回调
            getMessage().imMessage.state = ImMessage.State.LOADING
            // 发生成功了，清空临时未读列表
            getMessage().imMessage.unReadUserName.clear()
        } else {
            // 发送失败
            getMessage().imMessage.state = ImMessage.State.FAIL
        }
    }

    /**
     * 通知更新消息id更换
     */
    private fun notifyChangeMessageId(oldMessageId: Long, newMessageId: Long) {
        mainHandler.post {
            ServiceManager.instance.imListeners.forEach {
                it.onChangeMessageId(oldMessageId, newMessageId)
            }
        }
    }

    override fun onTaskEnd(errType: Int, errCode: Int) {
        if (errCode != 0) {
            // 如果有错误（一般是网络问题）,设置为发送失败
            getMessage().imMessage.state = ImMessage.State.FAIL
        }
        // 保存到数据库中
        getMessage().save()
        // 通知更新
        notifyChangeState(
            getMessage().imMessage.conversationKey,
            getMessage().imMessage.id,
            getMessage().imMessage.state
        )
    }

}