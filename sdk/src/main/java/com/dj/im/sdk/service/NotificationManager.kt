package com.dj.im.sdk.service

import android.content.Context
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImConversation
import com.dj.im.sdk.listener.ImListener
import com.dj.im.sdk.task.HttpGetGroupInfoTask
import com.dj.im.sdk.task.HttpGetUserInfoByNames

/**
 * Create by ChenLei on 2020/12/16
 * Describe: 通知栏管理
 */
internal class NotificationManager(val mContext: Context) {

    init {
        // 监听消息推送
        ServiceManager.instance.imListeners.add(object : ImListener() {
            override fun onPushMessage(message: Message) {
                // 不是自己发送的消息推送过来，才通知
                if (!message.isSelfSend()) {
                    pushMessage(message)
                }
            }
        })
    }

    /**
     * 推送消息
     */
    private fun pushMessage(message: Message) {
        // 首先获取发送者信息
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return
        val fromUser = ServiceManager.instance.getDb()
            ?.getUser(ServiceManager.instance.mAppKey, userName, message.imMessage.fromUserName)
        if (fromUser != null) {
            getOtherSideInfo(fromUser.alias, message)
        } else {
            // 本地无用户信息，从网络上获取
            HttpGetUserInfoByNames(listOf(message.imMessage.fromUserName)).success {
                it.firstOrNull()?.let { f -> getOtherSideInfo(f.alias, message) }
            }.start()
        }
    }

    /**
     * 获取对方信息
     */
    private fun getOtherSideInfo(fromName: String, message: Message) {
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return
        if (message.imMessage.conversationType == ImConversation.Type.SINGLE) {
            notification(message.imMessage.id, fromName, message.imMessage.getSummaryDesc())
        } else if (message.imMessage.conversationType == ImConversation.Type.GROUP) {
            val groupId = message.imMessage.toUserName.toLong()
            val groupInfo = ServiceManager.instance.getDb()
                ?.getGroupInfo(ServiceManager.instance.mAppKey, userName, groupId)
            if (groupInfo != null) {
                notification(
                    message.imMessage.id,
                    groupInfo.name,
                    "$fromName:${message.imMessage.getSummaryDesc()}"
                )
            } else {
                HttpGetGroupInfoTask(listOf(groupId)).success {
                    it.firstOrNull()?.let { g ->
                        notification(
                            message.imMessage.id,
                            g.name,
                            "$fromName:${message.imMessage.getSummaryDesc()}"
                        )
                    }
                }.start()
            }
        }
    }

    /**
     * 通知栏提示
     */
    private fun notification(id: Long, title: String, text: String) {
        DJIM.notificationHandle?.invoke(id, title, text)
    }
}