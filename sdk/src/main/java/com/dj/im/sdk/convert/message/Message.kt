package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.GetUserInfoTask
import java.util.*

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 消息抽象
 */
abstract class Message(val imMessage: ImMessage) {

    /**
     * 获取来源方的用户信息
     */
    fun getFromUser(): ImUser? {
        ServiceManager.instance.getUserInfo()?.id?.let {
            val user = ServiceManager.instance.getDb()?.getUser(it, imMessage.fromId)
            if (user == null) {
                ServiceManager.instance.sendTask(GetUserInfoTask(imMessage.fromId))
            }
            return user
        }
        return null
    }

    /**
     * 用户接收方的用户信息
     */
    fun getToUser(): ImUser? {
        ServiceManager.instance.getUserInfo()?.id?.let {
            val user = ServiceManager.instance.getDb()?.getUser(it, imMessage.toId)
            if (user == null) {
                ServiceManager.instance.sendTask(GetUserInfoTask(imMessage.toId))
            }
            return user
        }
        return null
    }

    /**
     * 获取未读用户id
     */
    fun getUnReadUserIdList(): List<Long> {
        ServiceManager.instance.getUserInfo()?.id?.let {
            return ServiceManager.instance.getDb()?.getUnReadUserId(
                it,
                imMessage.id
            )!!.map { m -> m.unReadUserId }
        }
        return Collections.emptyList()
    }

    /**
     * 保存消息
     */
    open fun save() {
        imMessage.save()
    }
}