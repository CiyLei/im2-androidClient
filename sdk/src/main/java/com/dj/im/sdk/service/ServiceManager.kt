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
import com.dj.im.sdk.IMarsConnectListener
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.listener.IConnectListener


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
    // 登录回调
    private var mLoginListener: ((Int, String?) -> Unit)? = null
    // 连接情况回调
    private var mConnectListeners = ArrayList<IConnectListener>()
    private var mImService: IImService? = null
    // 监听连接情况
    private val mConnectListener = object : IMarsConnectListener.Stub() {
        override fun result(resultCode: Int, resultMessage: String) {
            mHandler.post {
                mLoginListener?.invoke(resultCode, resultMessage)
                mConnectListeners.forEach { it.result(resultCode, resultMessage) }
                // 如果失败了，就关闭Mars
                if (resultCode != ResultEnum.Success.code) {
                    mImService?.disconnect()
                }
                mLoginListener = null
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
    fun login(token: String, listener: ((Int, String?) -> Unit)? = null) {
        checkStartService()
        mLoginListener = listener
        mImService?.connect(token, mConnectListener)
    }

    /**
     * 退出登录
     */
    fun logout() {
        mImService?.disconnect()
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
    fun addConnectListener(listener: IConnectListener) {
        mConnectListeners.add(listener)
    }

    /**
     * 移除连接情况监听
     */
    fun removeConnectListener(listener: IConnectListener) {
        mConnectListeners.remove(listener)
    }

    /**
     * 清空连接情况监听
     */
    fun clearConnectListener() {
        mConnectListeners.clear()
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
                Log.e("ServiceManager", "ImService 开启失败")
            }
        }
    }

    /**
     * 连接ImService
     */
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        mImService = IImService.Stub.asInterface(service)
    }

    /**
     * 断开连接ImService
     */
    override fun onServiceDisconnected(name: ComponentName?) {
        mImService = null
    }
}