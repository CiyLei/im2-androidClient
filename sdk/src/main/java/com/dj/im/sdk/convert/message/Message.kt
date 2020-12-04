package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.GetUserInfoTask
import com.dj.im.sdk.task.HttpGetUserInfoByIds
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
                HttpGetUserInfoByIds(listOf(imMessage.fromId)).start()
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
                HttpGetUserInfoByIds(listOf(imMessage.toId)).start()
            }
            return user
        }
        return null
    }

    /**
     * 获取未读用户id
     */
    fun getUnReadUserIdList(): List<Long> {
        // 如果临时的未读列表不为空，则返回临时的未读列表
        // 因为这时messageId是随机的，所以并没有保存到数据库中，而是保存在内存中
        if (imMessage.unReadUserId.isNotEmpty()) return imMessage.unReadUserId
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