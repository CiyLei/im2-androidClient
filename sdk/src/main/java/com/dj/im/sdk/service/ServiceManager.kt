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
import com.dj.im.sdk.IDBDao
import com.dj.im.sdk.IImService
import com.dj.im.sdk.IMarsListener
import com.dj.im.sdk.ITask
import com.dj.im.sdk.convert.message.MessageConvertFactory
import com.dj.im.sdk.entity.ImUser
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

    lateinit var application: Application
    private lateinit var mAppId: String
    private lateinit var mAppSecret: String
    private lateinit var mDeviceCode: String
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
         * 离线监听
         */
        override fun onOffline(code: Int, message: String) {
            mHandler.post {
                imListeners.forEach { it.onOffline(code, message) }
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
            val message = getDb()?.getMessageForId(getUserInfo()?.id!!, messageId)
            if (message != null) {
                val convert = MessageConvertFactory.convert(message)
                mHandler.post {
                    imListeners.forEach { it.onPushMessage(convert) }
                }
            }
        }

        /**
         * 会话已读监听
         */
        override fun onChangeConversationRead(conversationKey: String) {
            mHandler.post {
                imListeners.forEach { it.onChangeConversationRead(conversationKey) }
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
        this.application = application
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
     * 获取用户信息
     */
    fun getUserInfo(): ImUser? = mImService?.userInfo

    /**
     * 发送消息
     */
    fun sendTask(task: ITask) {
        mImService?.sendTask(task)
    }

    fun getDb(): IDBDao? {
        return mImService?.dbDao
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
            val imIntent = Intent(application, ImService::class.java)
            imIntent.putExtra("appId", mAppId)
            imIntent.putExtra("appSecret", mAppSecret)
            imIntent.putExtra("deviceCode", mDeviceCode)
            application.startService(imIntent)
            if (!application.bindService(imIntent, this, Service.BIND_AUTO_CREATE)) {
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