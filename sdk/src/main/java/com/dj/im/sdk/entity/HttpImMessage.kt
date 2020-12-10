package com.dj.im.sdk.entity

import java.io.Serializable

/**
 * Create by ChenLei on 2020/10/21
 * Describe: http的消息对象
 */
internal data class HttpImMessage(
    val id: Long,
    val conversationKey: String,
    val conversationType: Int,
    val fromUserName: String,
    val toUserName: String,
    val type: Int,
    val data: String,
    val summary: String,
    val createTime: Long,
    val unReadUserName: List<String>
) : Serializable {

    /**
     * 转换为ImMessage对象
     */
    fun toMessage(belongAppId: String, belongUserName: String): ImMessage {
        return ImMessage(
            belongAppId,
            belongUserName,
            id,
            conversationKey,
            conversationType,
            fromUserName,
            toUserName,
            type,
            data,
            summary,
            createTime = createTime,
            unReadUserName = ArrayList(unReadUserName)
        )
    }
}