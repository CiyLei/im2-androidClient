package com.dj.im.sdk.entity

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create by ChenLei on 2020/10/21
 * Describe: 历史记录的消息对象
 */
internal data class HistoryMessage(
    val id: Long,
    val conversationKey: String,
    val conversationType: Int,
    val fromId: Long,
    val toId: Long,
    val type: Int,
    val data: String,
    val summary: String,
    val createTime: String,
    val unReadUserId: List<Long>
) : Serializable {

    companion object {
        const val DATE_TO_STRING_DETAIAL_PATTERN = "yyyy-MM-dd HH:mm:ss"
    }

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
            createTime = SimpleDateFormat(DATE_TO_STRING_DETAIAL_PATTERN, Locale.CHINA).parse(
                createTime
            ).time
        )
    }
}