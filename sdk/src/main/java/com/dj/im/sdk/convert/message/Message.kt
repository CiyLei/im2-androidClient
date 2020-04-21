package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.service.ServiceManager

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
            return ServiceManager.instance.getDb()?.getUser(it, imMessage.fromId)
        }
        return null
    }

    /**
     * 用户接收方的用户信息
     */
    fun getToUser(): ImUser? {
        ServiceManager.instance.getUserInfo()?.id?.let {
            return ServiceManager.instance.getDb()?.getUser(it, imMessage.toId)
        }
        return null
    }

    /**
     * 保存消息
     */
    open fun save() {
        imMessage.save()
    }
}