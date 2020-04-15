package com.dj.im.sdk.conversation

import com.dj.im.sdk.entity.ImMessage

/**
 * Create by ChenLei on 2020/4/14
 * Describe: 会话抽象
 */
interface IConversation {
    /**
     * 发送消息
     */
    fun sendMessage(message: ImMessage)

    /**
     * 生成会话Id
     */
    fun generateConversationId(): String

    /**
     * 返回会话类型
     */
    fun getConversationType(): Int
}