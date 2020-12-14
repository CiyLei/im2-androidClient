package com.dj.im

import android.content.Intent
import android.provider.Settings
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
class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
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
            }

            override fun onFailure(p0: String?, p1: String?) {
                Log.d("MyApp", "注册友盟失败，$p0，$p1")
            }
        })
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
        DJIM.init(
            this,
            "e97f8917-272a-4ea2-a411-b66a33644368",
            "1fda7a5e-beb6-4ea8-9cee-918c71b0a8e5",
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        )
    }
}