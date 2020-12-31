package com.dj.im.sdk.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.dj.im.sdk.Constant
import com.dj.im.sdk.IMarsListener
import com.dj.im.sdk.ITask
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.db.ImDbDao
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.entity.ServerSituationEntity
import com.dj.im.sdk.service.handler.*
import com.google.gson.Gson
import com.tencent.mars.BaseEvent
import com.tencent.mars.Mars
import com.tencent.mars.app.AppLogic
import com.tencent.mars.sdt.SdtLogic
import com.tencent.mars.stn.StnLogic
import java.util.concurrent.ConcurrentHashMap


/**
 * Create by ChenLei on 2020/4/11
 * Describe: Im服务
 */
internal class ImService : Service() {

    companion object {
        // 随便自定义一个域名
        const val HOST = "localhost"

        // 客户端版本
        const val CLIENT_VERSION = 200
    }

    // 推荐连接的服务器信息
    var serverList: ServerSituationEntity? = null

    // 用户信息
    var userInfo: ImUser? = null

    // app应用id
    lateinit var appKey: String

    // app秘钥
    lateinit var appSecret: String

    // 设备码
    var deviceCode: String = ""

    // 回调列表
    var marsListener: IMarsListener? = null

    // 发送任务列表
    val tasks: ConcurrentHashMap<Int, ITask> = ConcurrentHashMap()

    // 数据库Dao
    lateinit var dbDao: ImDbDao

    // 推送消息处理器
    val pushHandler = HashMap<Int, IPushHandler>()

    // 处于登录验证
    var isLoginVerification = false

    // 是否连接中
    var isConnected = false

    lateinit var imServiceStub: ImServiceStub

    private val mGson = Gson()

    override fun onBind(intent: Intent?): IBinder? {
        imServiceStub = ImServiceStub(this)
        return imServiceStub
    }

    override fun onCreate() {
        super.onCreate()
        ServiceManager.instance.application = application
        pushHandler[Constant.CMD.PUSH_MESSAGE] = PushMessageHandler(this)
        pushHandler[Constant.CMD.PUSH_CONVERSATION] = PushConversationHandler(this)
        pushHandler[Constant.CMD.PUSH_READ_CONVERSATION] = PushReadConversationHandler(this)
        pushHandler[Constant.CMD.OFFLINE] = OfflineHandler(this)
        Mars.loadDefaultMarsLibrary()
        dbDao = ImDbDao(this)
        ServiceManager.instance.dbDao = dbDao
    }

    override fun onDestroy() {
        closeMars()
        marsListener = null
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        appKey = intent!!.getStringExtra("appKey")
        appSecret = intent.getStringExtra("appSecret")
        ServiceManager.instance.mAppKey = appKey
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 开启Mars服务
     */
    fun openMars(token: String) {
        // 先关闭Mars
        closeMars()
        // 推荐服务器不为空
        if (serverList != null && serverList!!.recommend.isNotEmpty()) {
            val callBack = MarsCallBack(this, token)
            // 设置回调事件
            AppLogic.setCallBack(callBack)
            StnLogic.setCallBack(callBack)
            SdtLogic.setCallBack(callBack)
            // 初始化Mars的平台连接
            Mars.init(applicationContext, Handler(Looper.getMainLooper()))
            // 初始化Mars
            StnLogic.setLonglinkSvrAddr(HOST, serverList!!.ports.toIntArray())
            // 设置备用地址
            StnLogic.setBackupIPs(
                HOST,
                Array(serverList!!.all.size) { serverList!!.all[it] })
            StnLogic.setClientVersion(CLIENT_VERSION)
            Mars.onCreate(true)
            // 开始连接
            BaseEvent.onForeground(true)
            StnLogic.makesureLongLinkConnected()
        } else {
            // 失败
            marsListener?.onLogin(ResultEnum.Error_Empty.code, ResultEnum.Error_Empty.message)
        }
    }

    /**
     * 关闭Mars服务
     */
    fun closeMars() {
        StnLogic.clearTask()
        Mars.onDestroy()
    }

    /**
     * 移除token
     */
    fun clearToken() {
        dbDao.deleteConfig(Constant.Key.TOKEN)
        dbDao.deleteConfig(Constant.Key.LAST_LOGIN_USER)
    }

    /**
     * 保存最后一次登录的用户信息
     */
    fun saveLastLoginUser() {
        userInfo?.let {
            dbDao.putConfigValue(Constant.Key.LAST_LOGIN_USER, mGson.toJson(it))
        }
    }

}