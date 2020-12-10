package com.dj.im.sdk.utils

import com.dj.im.sdk.entity.ImGroup
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.proto.PrGroup
import com.dj.im.sdk.proto.PrPushMessage
import com.dj.im.sdk.proto.PrUser

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 消息转换工具类
 */
object MessageConvertUtil {

    fun prPushMessage2ImMessage(
        belongAppId: String,
        belongUserName: String,
        prMessage: PrPushMessage.PushMessageResponse
    ): ImMessage {
        return ImMessage(
            belongAppId,
            belongUserName,
            prMessage.id,
            prMessage.conversationKey,
            prMessage.conversationType,
            prMessage.fromUserName,
            prMessage.toUserName,
            prMessage.type,
            prMessage.data,
            prMessage.summary,
            prMessage.createTime,
            ImMessage.State.SUCCESS
        )
    }

    fun prUser2ImUser(
        belongAppId: String,
        belongUserName: String,
        prUser: PrUser.UserResponse
    ): ImUser {
        return ImUser(
            belongAppId,
            belongUserName,
            prUser.userId,
            prUser.userName,
            prUser.alias,
            prUser.avatarUrl,
            prUser.extra
        )
    }

    fun prUser2ImGroup(
        belongAppId: String,
        belongUserName: String,
        prUser: PrGroup.GroupResponse
    ): ImGroup {
        return ImGroup(
            belongAppId,
            belongUserName,
            prUser.groupId,
            prUser.name,
            prUser.avatarUrl,
            prUser.userNameListList
        )
    }
}