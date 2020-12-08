package com.dj.im.sdk.entity

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create by ChenLei on 2020/10/21
 * Describe: http的消息对象
 */
internal data class HttpImMessage(
    val id: Long,
    val conversationKey: String,
    val conversationType: Int,
    val fromId: Long,
    val toId: Long,
    val type: Int,
    val data: String,
    val summary: String,
    val createTime: Long,
    val unReadUserId: List<Long>
) : Serializable {

    /**
     * 转换为ImMessage对象
     */
    fun toMessage(userId: Long): ImMessage {
        return ImMessage(
            id,
            conversationKey,
            conversationType,
            fromId,
            toId,
            type,
            data,
            summary,
            userId = userId,
            createTime = createTime
        )
    }
}