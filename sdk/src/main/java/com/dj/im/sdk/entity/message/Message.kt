package com.dj.im.sdk.entity.message;

import android.os.Handler
import com.dj.im.sdk.Constant;
import com.dj.im.sdk.ITask;
import com.dj.im.sdk.message.ResponseMessage
import com.dj.im.sdk.message.SendMessage
import com.dj.im.sdk.service.ServiceManager
import java.util.*
import kotlin.random.Random

/**
 * Create by ChenLei on 2020/4/17
 * Describe: 消息基类
 */
open class Message : ITask.Stub() {

    /**
     * 消息id
     */
    var id = 0L
    /**
     * 会话id（单聊:MD5(低位用户id + 高位用户id)，群聊:群Id）
     */
    var conversationId = ""
    /**
     * 会话类别（0:单聊、1:群聊）
     */
    var conversationType = Constant.ConversationType.SINGLE
    /**
     * 发送方用户id
     */
    var fromId = 0L
    /**
     * 接收方用户id（群聊为空）
     */
    var toId = 0L
    /**
     * 消息类别（0:文字，1:图片，2:视频，3:语音，1000+:定为自定义消息体）
     */
    var type = Constant.MessageType.TEXT
    /**
     * 消息内容（如果类型复杂，可以是json，但最好提取出摘要放入summary字段以便搜索）
     */
    var data = ""
    /**
     * 消息内容的摘要（作为为消息记录的搜索字段，如果这字段为空则以data字段进行搜索）
     */
    var summary = ""
    /**
     * 创建时间
     */
    var createTime = Date()
    /**
     * 发送状态
     * 0:发送成功、接收成功；1:发送中；2:发送失败
     */
    var state = Constant.MessageSendState.SUCCESS
    /**
     * 是否已读
     */
    var isRead = false

    override fun onReq2Buf(): ByteArray = SendMessage.SendMessageRequest.newBuilder()
        .setConversationId(conversationId).setConversationType(conversationType)
        .setFromId(fromId).setToId(toId).setType(type).setData(data).setSummary(summary)
        .build().toByteArray()

    override fun onBuf2Resp(buf: ByteArray?) {
        val response = ResponseMessage.Response.parseFrom(buf)
        if (response.success) {
            val result = SendMessage.SendMessageResponse.parseFrom(response.data)
            id = result.id
            createTime = Date(result.createTime)
            // 还是发送中的状态，等kafka的回调
            state = Constant.MessageSendState.LOADING
        } else {
            // 发送失败，随机给个id，反正不会存放后台的
            id = Random.nextLong()
            createTime = Date()
            state = Constant.MessageSendState.FAIL
        }
    }

    override fun onTaskEnd(errType: Int, errCode: Int) {
        if (errCode != 0 && id == 0L) {
            // 如果有错误，而且id还没指定的话（一般是网络问题）,设置为发送失败
            id = Random.nextLong()
            createTime = Date()
            state = Constant.MessageSendState.FAIL
        }
        // 保存到数据库中
        ServiceManager.instance.getUserId()?.let {
            ServiceManager.instance.conversationDao.addMessage(it, this)
        }
        // 在主线程中触发更改状态的回调
        Handler().post {
            ServiceManager.instance.imListeners.forEach { it.onChangeMessageSendState(id, state) }
        }
    }
}
