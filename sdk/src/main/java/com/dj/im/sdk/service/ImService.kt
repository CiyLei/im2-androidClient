package com.dj.im.sdk.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.dj.im.sdk.IMarsConnectListener
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.entity.ServerSituationEntity
import com.tencent.mars.BaseEvent
import com.tencent.mars.Mars
import com.tencent.mars.app.AppLogic
import com.tencent.mars.sdt.SdtLogic
import com.tencent.mars.stn.StnLogic


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
        // 推荐连接的服务器信息
        var SERVER_SITUATION: ServerSituationEntity? = null

        lateinit var APP_ID: String
        lateinit var APP_SECRET: String
        lateinit var DEVICE_CODE: String
    }

    override fun onBind(intent: Intent?): IBinder? = ImServiceStub(this)

    override fun onCreate() {
        super.onCreate()
        Mars.loadDefaultMarsLibrary()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        APP_ID = intent!!.getStringExtra("appId")
        APP_SECRET = intent.getStringExtra("appSecret")
        DEVICE_CODE = intent.getStringExtra("deviceCode")
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 开启Mars服务
     */
    fun openMars(token: String, listener: IMarsConnectListener? = null) {
        // 推荐服务器不为空
        if (SERVER_SITUATION != null && SERVER_SITUATION!!.recommend.isNotEmpty()) {
            val callBack = MarsCallBack(this, token, listener)
            // 设置回调事件
            AppLogic.setCallBack(callBack)
            StnLogic.setCallBack(callBack)
            SdtLogic.setCallBack(callBack)
            // 初始化Mars的平台连接
            Mars.init(applicationContext, Handler(Looper.getMainLooper()))
            // 初始化Mars
            StnLogic.setLonglinkSvrAddr(HOST, SERVER_SITUATION!!.ports.toIntArray())
            // 设置备用地址
            StnLogic.setBackupIPs(
                HOST,
                Array(SERVER_SITUATION!!.all.size) { SERVER_SITUATION!!.all[it] })
            StnLogic.setClientVersion(CLIENT_VERSION)
            Mars.onCreate(true)
            // 开始连接
            BaseEvent.onForeground(true)
            StnLogic.makesureLongLinkConnected()
        } else {
            // 失败
            listener?.result(ResultEnum.Error_Empty.code, ResultEnum.Error_Empty.message)
        }
    }

    /**
     * 关闭Mars服务
     */
    fun closeMars() {
        StnLogic.clearTask()
        Mars.onDestroy()
    }

}