package com.dj.im.sdk

import android.app.ActivityManager
import android.app.Application
import android.content.Context.ACTIVITY_SERVICE
import android.os.Process
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.conversation.GroupConversation
import com.dj.im.sdk.conversation.SingleConversation
import com.dj.im.sdk.convert.conversation.ConversationConvertFactory
import com.dj.im.sdk.entity.ImGroup
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.listener.ImListener
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.HttpGetGroupInfoTask
import com.dj.im.sdk.task.HttpGetUserInfoByNames
import com.dj.im.sdk.task.RevokeMessageTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * Create by ChenLei on 2020/4/11
 * Describe: DJIM 入口类
 */
object DJIM {

    // 友盟自定义通知栏id
    const val UMENG_IM_BUILDER_ID = 3941

    // 是否自动登录
    var isAutoLogin = false

    // 是否初始化过
    private var initd = false

    // 通知栏处理器
    var notificationHandle: ((Long, String, String) -> Unit)? = null

    /**
     * 初始化SDK
     *
     * @param context 上下文
     * @param appKey AppId
     */
    fun init(application: Application, appKey: String, appSecret: String) {
        // 只在主进程中初始化
        if (application.packageName == getCurProcessName(application)) {
            initd = true
            ServiceManager.instance.init(application, appKey, appSecret)
        }
    }

    /**
     * 登录
     *
     * @param token 登录Token
     */
    fun login(token: String) {
        ServiceManager.instance.login(token)
    }

    /**
     * 退出登录
     */
    fun logout() {
        ServiceManager.instance.logout()
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(): ImUser? {
        return ServiceManager.instance.getUserInfo()
    }

    /**
     * IM消息回调接口
     */
    fun getImListeners(): ArrayList<ImListener> = ServiceManager.instance.imListeners

    /**
     * 返回单聊的会话
     */
    fun getSingleConversation(toUserName: String): SingleConversation {
        return (getAllConversations().find { it is SingleConversation && it.toUserName == toUserName } as? SingleConversation)
            ?: SingleConversation(toUserName)
    }

    /**
     * 返回群聊的会话
     */
    fun getGroupConversation(groupId: Long): GroupConversation {
        return (getAllConversations().find { it is GroupConversation && it.groupId == groupId } as? GroupConversation)
            ?: GroupConversation(groupId)
    }

    /**
     * 设置设备唯一识别码
     */
    fun setDeviceCode(deviceCode: String) {
        ServiceManager.instance.setDeviceCode(deviceCode)
    }

    /**
     * 获取所有的会话
     */
    fun getAllConversations(): List<Conversation> {
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return emptyList()
        val conversations = ServiceManager.instance.getDb()
            ?.getConversations(ServiceManager.instance.mAppKey, userName)
        return conversations?.mapNotNull { ConversationConvertFactory.convert(it) } ?: emptyList()
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(userName: String): ImUser? {
        val loginUserName = ServiceManager.instance.getUserInfo()?.userName ?: return null
        val result = ServiceManager.instance.getDb()
            ?.getUser(ServiceManager.instance.mAppKey, loginUserName, userName)
        if (result == null) {
            // 如果在本地无法找到用户的信息，那就从网络获取
            HttpGetUserInfoByNames(listOf(userName)).start()
        }
        return result
    }

    /**
     * 获取群信息
     */
    fun getGroupInfo(groupId: Long): ImGroup? {
        ServiceManager.instance.getUserInfo()?.let {
            val groupInfo = ServiceManager.instance.getDb()
                ?.getGroupInfo(ServiceManager.instance.mAppKey, it.userName, groupId)
            if (groupInfo == null) {
                HttpGetGroupInfoTask(listOf(groupId)).start()
            }
            return groupInfo
        }
        return null
    }

    /**
     * 撤回消息
     */
    fun revokeMessage(messageId: Long) {
        ServiceManager.instance.sendTask(RevokeMessageTask(messageId))
    }

    /**
     * 获取默认的线程池
     */
    internal fun getDefaultThreadPoolExecutor(): ExecutorService = Executors.newCachedThreadPool()

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