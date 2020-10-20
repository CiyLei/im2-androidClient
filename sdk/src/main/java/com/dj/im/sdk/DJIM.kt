package com.dj.im.sdk

import android.app.ActivityManager
import android.app.Application
import android.content.Context.ACTIVITY_SERVICE
import android.os.Process
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.conversation.GroupConversation
import com.dj.im.sdk.conversation.SingleConversation
import com.dj.im.sdk.convert.conversation.ConversationConvertFactory
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.listener.ImListener
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.GetUserInfoTask


/**
 * Create by ChenLei on 2020/4/11
 * Describe: DJIM 入口类
 */
object DJIM {

    private var initd = false

    /**
     * 初始化SDK
     *
     * @param context 上下文
     * @param appId AppId
     * @param appSecret App秘钥
     */
    fun init(application: Application, appId: String, appSecret: String, deviceCode: String) {
        initd = true
        ServiceManager.instance.init(application, appId, appSecret, deviceCode)
    }

    /**
     * 断言是否初始化
     */
    private fun assertionInit() {
        if (initd)
            return
        throw ExceptionInInitializerError("DJIM SDK 未初始化")
    }

    /**
     * 登录
     *
     * @param token 登录Token
     */
    fun login(token: String) {
        assertionInit()
        ServiceManager.instance.login(token)
    }

    /**
     * 退出登录
     */
    fun logout() {
        assertionInit()
        ServiceManager.instance.logout()
    }

    /**
     * 设置是否在前台
     */
    fun onForeground(foreground: Boolean) {
        if (initd) {
            ServiceManager.instance.onForeground(foreground)
        }
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(): ImUser? {
        assertionInit()
        return ServiceManager.instance.getUserInfo()
    }

    /**
     * IM消息回调接口
     */
    fun getImListeners(): ArrayList<ImListener> = ServiceManager.instance.imListeners

    /**
     * 返回单聊的会话
     */
    fun getSingleConversation(toUserId: Long): SingleConversation {
        assertionInit()
        return (getAllConversations().find { it is SingleConversation && it.toUserId == toUserId } as? SingleConversation)
            ?: SingleConversation(toUserId)
    }

    /**
     * 返回群聊的会话
     */
    fun getGroupConversation(groupId: Long): GroupConversation {
        assertionInit()
        return (getAllConversations().find { it is GroupConversation && it.groupId == groupId } as? GroupConversation)
            ?: GroupConversation(groupId)
    }

    /**
     * 获取所有的会话
     */
    fun getAllConversations(): List<Conversation> {
        assertionInit()
        ServiceManager.instance.getUserInfo()?.id?.let {
            val conversations = ServiceManager.instance.getDb()?.getConversations(it)
            val result = ArrayList<Conversation>()
            conversations?.forEach { c ->
                val convert = ConversationConvertFactory.convert(c)
                if (convert != null) {
                    result.add(convert)
                }
            }
            return result
        }
        return emptyList()
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(userId: Long): ImUser? {
        ServiceManager.instance.getUserInfo()?.id?.let {
            val result = ServiceManager.instance.getDb()?.getUser(it, userId)
            if (result == null) {
                // 如果在本地无法找到用户的信息，那就从网络获取
                ServiceManager.instance.sendTask(GetUserInfoTask(userId))
            }
            return result
        }
        return null
    }

    /**
     * 获取当前进程的进程名
     */
    private fun getCurProcessName(application: Application): String? {
        val pid = Process.myPid()
        val mActivityManager = application.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (runningAppProcess in mActivityManager.runningAppProcesses) {
            if (runningAppProcess.pid == pid) {
                return runningAppProcess.processName
            }
        }
        return null
    }
}