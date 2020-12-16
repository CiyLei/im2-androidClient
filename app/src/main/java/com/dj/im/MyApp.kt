package com.dj.im

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import android.util.Log
import android.widget.Toast
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.listener.ImListener
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import org.android.agoo.xiaomi.MiPushRegistar


/**
 * Create by ChenLei on 2020/4/11
 * Describe:
 */
class MyApp : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    companion object {
        /**
         * 在前台获取的信息
         * 为什么要这个呢
         * 因为友盟推送的消息会延迟，所以要将前台获取的消息记录下来，这是友盟延迟推过来的时候就可以不显示了
         */
        val frontMessage = HashSet<String>()

        var createCount = 0
    }

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
                // 记录前台收到的消息
                if (createCount > 0) {
                    frontMessage.add(message.imMessage.id.toString())
                }
            }
        })
        // 设置自动登录
        DJIM.isAutoLogin = true

        MiPushRegistar.register(this, "2882303761518896440", "5131889688440")
        UMConfigure.init(
            this,
            "5fd6baaca78ec8332fa66fe7",
            "Umeng",
            UMConfigure.DEVICE_TYPE_PHONE,
            "1845aab41b963c25de66dd2d75e2c5a7"
        )
//        MessageSharedPrefs.getInstance(this).displayNotificationNumber = Int.MAX_VALUE
        PushAgent.getInstance(this).displayNotificationNumber = 10
        PushAgent.getInstance(this).setNotificaitonOnForeground(false);
        PushAgent.getInstance(this).messageHandler = ImMessageHandler(this)
        PushAgent.getInstance(this).register(object : IUmengRegisterCallback {
            override fun onSuccess(p0: String?) {
                Log.d("MyApp", "注册友盟成功，设备唯一识别码:$p0")
                imInit(p0 ?: "")
            }

            override fun onFailure(p0: String?, p1: String?) {
                Log.d("MyApp", "注册友盟失败，$p0，$p1")
                imInit("")
            }
        })
    }

    private fun imInit(deviceCode: String) {
        DJIM.init(
            this,
            "e97f8917-272a-4ea2-a411-b66a33644368",
            "1fda7a5e-beb6-4ea8-9cee-918c71b0a8e5",
            deviceCode
        )
    }


    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity?) {
        createCount++
        if (createCount == 1) {
            frontMessage.clear()
        }
    }

    override fun onActivityResumed(p0: Activity?) {
    }

    override fun onActivityPaused(p0: Activity?) {
    }

    override fun onActivityStopped(p0: Activity?) {
        createCount--
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
    }

    override fun onActivityDestroyed(p0: Activity?) {
    }
}