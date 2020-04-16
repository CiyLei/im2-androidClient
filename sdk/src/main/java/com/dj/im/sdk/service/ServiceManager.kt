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
import com.dj.im.sdk.*
import com.dj.im.sdk.Constant
import com.dj.im.sdk.message.Message
import com.dj.im.sdk.listener.IImListener
import com.dj.im.sdk.message.PushMessage
import com.dj.im.sdk.message.ResponseMessage
import java.util.*
import kotlin.collections.ArrayList


/**
 * Create by ChenLei on 2020/4/11
 * Describe: 服务管理类
 */
internal class ServiceManager : ServiceConnection {

    companion object {
        // 线程安全获取单例
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ServiceManager()
        }
    }

    private lateinit var mAppId: String
    private lateinit var mAppSecret: String
    private lateinit var mDeviceCode: String
    private lateinit var mApplication: Application
    private var mHandler = Handler(Looper.getMainLooper())
    // 连接情况回调
    private var mImListeners = ArrayList<IImListener>()
    private var mImService: IImService? = null
    // 监听Mars的回调
    private val mMarsListener = object : IMarsListener.Stub() {
        override fun onPush(cmdId: Int, data: ByteArray?) {
            Log.d("ServiceManager", "接收到推送内容,data:${Arrays.toString(data)}")
            val response = ResponseMessage.Response.parseFrom(data)
            when (cmdId) {
                Constant.CMD.PUSH_MESSAGE -> {
                    // 有推送消息
                    val pushResponse = PushMessage.PushMessageResponse.parseFrom(response.data)
                    val message = Message(pushResponse)
                    mHandler.post {
                        mImListeners.forEach { it.onPushMessage(message) }
                    }
                }
            }
        }

        override fun onConnect(code: Int, message: String) {
            mHandler.post {
                mImListeners.forEach { it.onConnect(code, message) }
            }
        }
    }

    private fun ServiceManager() {}

    /**
     * 初始化
     */
    fun init(application: Application, appId: String, appSecret: String, deviceCode: String) {
        mApplication = application
        mAppId = appId
        mAppSecret = appSecret
        mDeviceCode = deviceCode
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
     * 发送消息
     */
    fun sendMessage(cmdId: Int, messageData: ByteArray) {
        mImService?.sendMessage(cmdId, messageData)
    }

    /**
     * 设置是否在前台
     */
    fun onForeground(foreground: Boolean) {
        checkStartService()
        mImService?.onForeground(foreground)
    }

    /**
     * 添加连接情况监听
     */
    fun addImListener(listener: IImListener) {
        mImListeners.add(listener)
    }

    /**
     * 移除连接情况监听
     */
    fun removeImListener(listener: IImListener) {
        mImListeners.remove(listener)
    }

    /**
     * 清空连接情况监听
     */
    fun clearImListener() {
        mImListeners.clear()
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