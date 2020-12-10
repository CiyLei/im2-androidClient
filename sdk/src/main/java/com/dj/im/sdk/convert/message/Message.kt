package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.HttpGetUserInfoByNames

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 消息抽象
 */
abstract class Message(val imMessage: ImMessage) {

    /**
     * 这条消息是否是自己发送的
     */
    fun isSelfSend(): Boolean {
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return false
        return userName == imMessage.fromUserName
    }

    /**
     * 获取来源方的用户信息
     */
    fun getFromUser(): ImUser? {
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return null
        val user = ServiceManager.instance.getDb()
            ?.getUser(ServiceManager.instance.mAppId, userName, imMessage.fromUserName)
        if (user == null) {
            HttpGetUserInfoByNames(listOf(imMessage.fromUserName)).start()
        }
        return user
    }

    /**
     * 用户接收方的用户信息
     */
    fun getToUser(): ImUser? {
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return null
        val user = ServiceManager.instance.getDb()
            ?.getUser(ServiceManager.instance.mAppId, userName, imMessage.toUserName)
        if (user == null) {
            HttpGetUserInfoByNames(listOf(imMessage.fromUserName)).start()
        }
        return user
    }

    /**
     * 获取未读用户id
     */
    fun getUnReadUserIdList(): List<String> {
        // 如果临时的未读列表不为空，则返回临时的未读列表
        // 因为这时messageId是随机的，所以并没有保存到数据库中，而是保存在内存中
        if (imMessage.unReadUserName.isNotEmpty()) return imMessage.unReadUserName
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return emptyList()
        return ServiceManager.instance.getDb()?.getUnReadUserId(
            ServiceManager.instance.mAppId, userName, imMessage.id
        )?.map { m -> m.unReadUserName } ?: emptyList()
    }

    /**
     * 保存消息
     */
    open fun save() {
        imMessage.save()
    }
}