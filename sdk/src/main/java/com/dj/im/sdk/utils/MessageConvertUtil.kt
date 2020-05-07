package com.dj.im.sdk.utils

import com.dj.im.sdk.entity.ImGroup
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.proto.PrGroup
import com.dj.im.sdk.proto.PrPushMessage
import com.dj.im.sdk.proto.PrUser
import java.util.*

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 消息转换工具类
 */
object MessageConvertUtil {

    fun prPushMessage2ImMessage(prMessage: PrPushMessage.PushMessageResponse): ImMessage {
        return ImMessage(
            prMessage.id,
            prMessage.conversationKey,
            prMessage.conversationType,
            prMessage.fromId,
            prMessage.toId,
            prMessage.type,
            prMessage.data,
            prMessage.summary,
            prMessage.createTime,
            ImMessage.State.SUCCESS
        )
    }

    fun prUser2ImUser(prUser: PrUser.UserResponse): ImUser {
        return ImUser(prUser.userId, prUser.userName, prUser.alias, prUser.avatarUrl, prUser.extra)
    }

    fun prUser2ImGroup(prUser: PrGroup.GroupResponse): ImGroup {
        return ImGroup(
            prUser.groupId,
            prUser.name,
            prUser.avatarUrl,
            prUser.userIdListList
        )
    }
}