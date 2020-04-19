package com.dj.im.sdk.listener

import com.dj.im.sdk.task.message.Message


/**
 * Create by ChenLei on 2020/4/12
 * Describe: 监听连接情况的回调
 */
open class ImListener {

    /**
     * 登录回调
     */
    open fun onLogin(code: Int, message: String) {}

    /**
     * 有消息推送的回调
     */
    open fun onPushMessage(message: Message) {}

    /**
     * 某条消息的发送状态发生了变化
     * @param messageId 消息id
     * @param state 消息状态
     */
    open fun onChangeMessageSendState(messageId: Long, state: Int) {}

    /**
     * 会话发生了变化
     */
    open fun onChangeConversions() {}

    /**
     * 会话已读监听
     */
    open fun onChangeConversationRead(conversationId: String) {}
}
