package com.dj.im.sdk.service

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import com.dj.im.sdk.*
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
    internal lateinit var mAppKey: String
    private lateinit var mAppSecret: String
    private lateinit var mDeviceCode: String
    private var mHandler = Handler(Looper.getMainLooper())

    // 待执行任务
    private val mPendingTask = ArrayList<Runnable>()

    // 连接情况回调
    internal var imListeners = ArrayList<ImListener>()

    // IM服务端
    private var mImService: IImService? = null

    // 监听Mars的回调
    private val mMarsListener = object : IMarsListener.Stub() {

        /**
         * 消息发送状态改变
         */
        override fun onChangeMessageState(conversationKey: String, messageId: Long, state: Int) {
            mHandler.post {
                imListeners.forEach {
                    it.onChangeMessageSendState(
                        conversationKey,
                        messageId,
                        state
                    )
                }
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
            val userName = getUserInfo()?.userName ?: return
            val message = getDb()?.getMessageForId(mAppKey, userName, messageId)
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
    fun init(application: Application, appKey: String, appSecret: String, deviceCode: String) {
        this.application = application.also {
            it.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                }

                override fun onActivityStarted(activity: Activity?) {
                }

                override fun onActivityResumed(activity: Activity?) {
                    onForeground(true)
                }

                override fun onActivityPaused(activity: Activity?) {
                    onForeground(false)
                }

                override fun onActivityStopped(activity: Activity?) {
                }

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                }

                override fun onActivityDestroyed(activity: Activity?) {
                }
            })
        }
        mAppKey = appKey
        mAppSecret = appSecret
        mDeviceCode = deviceCode
        checkStartService()
        NotificationManager(application)
    }

    /**
     * 登录
     *
     * @param token 登录Token
     */
    fun login(token: String) {
        checkStartService()
        // 如果服务还没初始化好，加入到待执行任务中
        synchronized(mPendingTask) {
            if (mImService == null) {
                mPendingTask.add(Runnable {
                    mImService?.setOnMarsListener(mMarsListener)
                    mImService?.login(token)
                })
            } else {
                mImService?.setOnMarsListener(mMarsListener)
                mImService?.login(token)
            }
        }
    }

    /**
     * 退出登录
     */
    fun logout() {
        // 如果服务还没初始化好，加入到待执行任务中
        synchronized(mPendingTask) {
            if (mImService == null) {
                mPendingTask.add(Runnable {
                    mImService?.setOnMarsListener(null)
                    mImService?.logout()
                })
            } else {
                mImService?.setOnMarsListener(null)
                mImService?.logout()
            }
        }
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(): ImUser? = mImService?.userInfo

    /**
     * 发送消息
     */
    fun sendTask(task: ITask) {
        // 如果服务还没初始化好，加入到待执行任务中
        synchronized(mPendingTask) {
            if (mImService == null) {
                mPendingTask.add(Runnable {
                    mImService?.sendTask(task)
                })
            } else {
                mImService?.sendTask(task)
            }
        }
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
        if (mImService == null && ::mAppKey.isInitialized && ::mAppSecret.isInitialized && ::mDeviceCode.isInitialized) {
            val imIntent = Intent(application, ImService::class.java)
            imIntent.putExtra("appKey", mAppKey)
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
        if (DJIM.isAutoLogin) {
            mImService?.autoConnect()
        }
        // 执行之前待执行的任务
        synchronized(mPendingTask) {
            mPendingTask.forEach { it.run() }
            mPendingTask.clear()
        }
    }

    /**
     * 断开连接ImService
     */
    override fun onServiceDisconnected(name: ComponentName?) {
        mImService?.setOnMarsListener(null)
        mImService = null
    }

    /**
     * 获取当前进程的进程名
     */
    private fun getCurProcessName(application: Application): String? {
        val pid = Process.myPid()
        val mActivityManager =
            application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (runningAppProcess in mActivityManager.runningAppProcesses) {
            if (runningAppProcess.pid == pid) {
                return runningAppProcess.processName
            }
        }
        return null
    }
}