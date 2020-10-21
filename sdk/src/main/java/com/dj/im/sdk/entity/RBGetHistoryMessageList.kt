package com.dj.im.sdk.entity

import java.io.Serializable

/**
 * Create by ChenLei on 2020/10/21
 * Describe: 获取历史消息列表的请求数据
 */
data class RBGetHistoryMessageList(val conversationKey: String, val messageId: Long) : Serializable