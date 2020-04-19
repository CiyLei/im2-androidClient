package com.dj.im.sdk.service

import android.app.Application
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.dj.im.sdk.IImService
import com.dj.im.sdk.IMarsListener
import com.dj.im.sdk.ITask
import com.dj.im.sdk.db.ConversationDao
import com.dj.im.sdk.task.message.Message
import com.dj.im.sdk.listener.ImListener


/**
 * Create by ChenLei on 2020/4/11
 * Describe: 服务管理类
 */
internal class ServiceManager private constructor() : ServiceConnection {

    companion object {
        // 线程安全获取单例
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ServiceManager()
        }
    }

    val conversationDao: ConversationDao by lazy { ConversationDao(mApplication) }

    private lateinit var mAppId: String
    private lateinit var mAppSecret: String
    private lateinit var mDeviceCode: String
    private lateinit var mApplication: Application
//    private lateinit var mConversationDao: ConversationDao
    private var mHandler = Handler(Looper.getMainLooper())

    // 连接情况回调
    internal var imListeners = ArrayList<ImListener>()

    // IM服务端
    private var mImService: IImService? = null

    // 监听Mars的回调
    private val mMarsListener = object : IMarsListener.Stub() {

        /**
         * 消息发送状态改变
         */
        override fun onChangeMessageState(messageId: Long, state: Int) {
            mHandler.post {
                imListeners.forEach { it.onChangeMessageSendState(messageId, state) }
            }
        }

        /**
         * 服务连接监听
         */
        override fun onConnect(code: Int, message: String) {
            mHandler.post {
                imListeners.forEach { it.onLogin(code, message) }
            }
        }

        /**
         * 消息推送监听
         */
        override fun onPushMessage(messageId: Long) {
            val message = conversationDao.getMessageForId(getUserId()!!, messageId)
            if (message != null) {
                mHandler.post {
                    imListeners.forEach { it.onPushMessage(message) }
                }
            }
        }

        /**
         * 会话已读监听
         */
        override fun onChangeConversationRead(conversationId: String) {
            mHandler.post {
                imListeners.forEach { it.onChangeConversationRead(conversationId) }
            }
        }

        /**
         * 会话列表状态监听
         */
        override fun onChangeConversions() {
            mHandler.post {
                imListeners.forEach { it.onChangeConversions() }
            }
        }
    }

    /**
     * 初始化
     */
    fun init(application: Application, appId: String, appSecret: String, deviceCode: String) {
        mApplication = application
        mAppId = appId
        mAppSecret = appSecret
        mDeviceCode = deviceCode
//        mConversationDao = ConversationDao(application)
        checkStartService()
    }

    /**
     * 登录
     *
     * @param token 登录Token
     */
    fun login(token: String) {
        checkStartService()
        mImService?.setOnMarsListener(mMarsListener)
        mImService?.connect(token)
    }

    /**
     * 退出登录
     */
    fun logout() {
        mImService?.setOnMarsListener(null)
        mImService?.disconnect()
    }

    /**
     * 获取用户id
     */
    fun getUserId(): Long? = mImService?.userId

    /**
     * 获取用户名
     */
    fun getUserName(): String? = mImService?.userName

    /**
     * 获取用户id
     */
    fun getAlias(): String? = mImService?.alias

    /**
     * 获取用户名
     */
    fun getAvatarUrl(): String? = mImService?.avatarUrl

    /**
     * 发送消息
     */
    fun sendTask(task: ITask) {
        mImService?.sendTask(task)
    }

    /**
     * 设置是否在前台
     */
    fun onForeground(foreground: Boolean) {
        checkStartService()
        mImService?.onForeground(foreground)
    }

    /**
     * 检查开启服务
     */
    private fun checkStartService() {
        if (mImService == null) {
            val imIntent = Intent(mApplication, ImService::class.java)
            imIntent.putExtra("appId", mAppId)
            imIntent.putExtra("appSecret", mAppSecret)
            imIntent.putExtra("deviceCode", mDeviceCode)
            mApplication.startService(imIntent)
            if (!mApplication.bindService(imIntent, this, Service.BIND_AUTO_CREATE)) {
                Log.e("ServiceManager", "【ImService 开启失败】")
            }
        }
    }

    /**
     * 连接ImService
     */
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        mImService = IImService.Stub.asInterface(service)
        mImService?.setOnMarsListener(mMarsListener)
        mImService?.autoConnect()
    }

    /**
     * 断开连接ImService
     */
    override fun onServiceDisconnected(name: ComponentName?) {
        mImService?.setOnMarsListener(null)
        mImService = null
    }
}