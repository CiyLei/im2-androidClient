package com.dj.im

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.listener.ImListener
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.xiaomi.mipush.sdk.MiPushClient
import org.android.agoo.huawei.HuaWeiRegister
import org.android.agoo.xiaomi.MiPushRegistar


/**
 * Create by ChenLei on 2020/4/11
 * Describe:
 */
class MyApp : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    companion object {
        private const val CHANNEL_ID = "djim_channel_id"
        private const val CHANNEL_NAME = "djim_channel_name"
    }

    private var mActivityCount = 0

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        DJIM.getImListeners().add(object : ImListener() {

            override fun onOffline(code: Int, message: String) {
                Toast.makeText(this@MyApp, message, Toast.LENGTH_SHORT).show()
                //指向登录界面
                val intent = Intent(this@MyApp, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            override fun onLogin(code: Int, message: String) {
                if (code != ResultEnum.Success.code) {
                    Toast.makeText(this@MyApp, message, Toast.LENGTH_SHORT).show()
                    // 回到登录页
                }
            }

            override fun onPushMessage(message: Message) {

            }
        })
        // 设置自动登录
//        DJIM.isAutoLogin = true
        // 通知栏
        DJIM.notificationHandle = { id, title, text -> notification(id, title, text) }
        DJIM.init(
            this,
            "e97f8917-272a-4ea2-a411-b66a33644368",
            "1fda7a5e-beb6-4ea8-9cee-918c71b0a8e5"
        )

        // 小米厂商推送集成
        MiPushRegistar.register(this, "2882303761518896440", "5131889688440")
        // 华为厂商推送集成
        HuaWeiRegister.register(this)

        UMConfigure.init(
            this,
            "5fd6baaca78ec8332fa66fe7",
            "Umeng",
            UMConfigure.DEVICE_TYPE_PHONE,
            "1845aab41b963c25de66dd2d75e2c5a7"
        )
//        MessageSharedPrefs.getInstance(this).displayNotificationNumber = Int.MAX_VALUE
        PushAgent.getInstance(this).apply {
            displayNotificationNumber = 10
            setNotificaitonOnForeground(false)
            messageHandler = ImMessageHandler(this@MyApp)
            register(object : IUmengRegisterCallback {
                override fun onSuccess(p0: String?) {
                    Log.d("MyApp", "注册友盟成功，设备唯一识别码:$p0")
                    Thread {
                        Thread.sleep(5000)
                        Log.d("MyApp", "重新设置设备码")
                        DJIM.setDeviceCode(p0 ?: "")
                    }.start()
                }

                override fun onFailure(p0: String?, p1: String?) {
                    Log.d("MyApp", "注册友盟失败，$p0，$p1")
                }
            })
        }
    }

    private fun notification(id: Long, title: String, text: String) {
        // 应用处于后台时，通知栏提示
        if (mActivityCount == 0) {
            //  android8.0 以上要创建一个通道
            var channel: NotificationChannel? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.enableLights(true);//是否在桌面icon右上角展示小红点
                channel.lightColor = Color.RED;//小红点颜色
                channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
            }

            // 创建通知
            val builder: NotificationCompat.Builder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationCompat.Builder(this, "djim")
                } else {
                    NotificationCompat.Builder(this)
                }
            //PendingIntent点击通知后所跳转的页面
            builder.setContentTitle(title)
            builder.setContentText(text)
            builder.setChannelId(CHANNEL_ID)
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setWhen(System.currentTimeMillis())
            builder.setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, ConversationActivity::class.java),
                    0
                )
            ) //执行intent
            builder.setDefaults(NotificationCompat.DEFAULT_ALL)
            val notification = builder.build() //将builder对象转换为普通的notification
            notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL //点击通知后通知消失

            val manager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            // android8.0 以上，创建通道
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager?.createNotificationChannel(channel!!)
            }
            // 开始通知
            manager!!.notify(id.hashCode(), notification)
        }
    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity?) {
        mActivityCount++
        // 清空通知
        (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)?.cancelAll()
        if (MiPushRegistar.checkDevice(this)) {
            MiPushClient.clearNotification(this)
        }
    }

    override fun onActivityResumed(p0: Activity?) {
    }

    override fun onActivityPaused(p0: Activity?) {
    }

    override fun onActivityStopped(p0: Activity?) {
        mActivityCount--
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
    }

    override fun onActivityDestroyed(p0: Activity?) {
    }
}