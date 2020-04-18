package com.dj.im.sdk

import android.app.ActivityManager
import android.app.Application
import android.content.Context.ACTIVITY_SERVICE
import android.os.Process
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.conversation.SingleConversation
import com.dj.im.sdk.entity.User
import com.dj.im.sdk.listener.ImListener
import com.dj.im.sdk.service.ServiceManager


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
     * 获取用户id
     */
    fun getUserId(): Long? {
        assertionInit()
        return ServiceManager.instance.getUserId()
    }

    /**
     * 获取用户名
     */
    fun getUserName(): String? {
        assertionInit()
        return ServiceManager.instance.getUserName()
    }

    /**
     * 用户别名
     */
    fun getAlias(): String? {
        assertionInit()
        return ServiceManager.instance.getAlias()
    }

    /**
     * 用户头像
     */
    fun getAvatarUrl(): String? {
        assertionInit()
        return ServiceManager.instance.getAvatarUrl()
    }

    /**
     * 添加连接情况监听
     */
    fun addImListener(listener: ImListener) {
        ServiceManager.instance.addImListener(listener)
    }

    /**
     * 移除连接情况监听
     */
    fun removeImListener(listener: ImListener) {
        ServiceManager.instance.removeImListener(listener)
    }

    /**
     * 清空连接情况监听
     */
    fun clearImListener() {
        ServiceManager.instance.clearImListener()
    }

    /**
     * 返回单聊的会话
     */
    fun getSingleConversation(toUser: User): Conversation {
        assertionInit()
        return SingleConversation(toUser)
    }

    /**
     * 获取所有的会话
     */
    fun getAllConversations(): List<Conversation> {
        assertionInit()
        ServiceManager.instance.getUserId()?.let {
            return ServiceManager.instance.conversationDao.getConversations(it)
        }
        return emptyList()
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