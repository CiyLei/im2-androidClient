package com.dj.im.sdk.message

import com.dj.im.sdk.message.PushMessage.PushMessageResponse
import java.util.*

/**
 * Create by ChenLei on 2020/4/13
 * Describe: 消息实体类
 */
open class Message constructor() {

    constructor(response: PushMessageResponse) : this() {
        id = response.id
        conversationId = response.conversationId
        conversationType = response.conversationType
        fromId = response.fromId
        toId = response.toId
        type = response.type
        data = response.data
        summary = response.summary
        createDate = Date(response.createTime)
    }

    /**
     * 消息id
     */
    var id: Long = 0
    /**
     * 会话id（单聊:MD5(低位用户id + 高位用户id)，群聊:群Id）
     */
    var conversationId: String = ""
    /**
     * 会话类别（0:单聊、1:群聊）
     */
    var conversationType = 0
    /**
     * 发送方用户id
     */
    var fromId: Long = 0
    /**
     * 接收方用户id（群聊为空）
     */
    var toId: Long = 0
    /**
     * 消息类别（0:文字，1:图片，2:视频，3:语音，1000+:定为自定义消息体）
     */
    var type = 0
    /**
     * 消息内容（如果类型复杂，可以是json，但最好提取出摘要放入summary字段以便搜索）
     */
    var data: String = ""
    /**
     * 消息内容的摘要（作为为消息记录的搜索字段，如果这字段为空则以data字段进行搜索）
     */
    var summary: String = ""
    /**
     * 发送时间
     */
    var createDate: Date = Date()
    /**
     * 消息状态
     */
    var state: Int = MessageState.NONE

    companion object {

        /**
         * 消息状态
         */
        object MessageState {
            const val NONE = 0
            const val SENDING = 1
            const val SEND_SUCCESS = 2
            const val SEND_FAIL = 3
        }
    }

}