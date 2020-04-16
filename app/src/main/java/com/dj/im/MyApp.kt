package com.dj.im

import android.provider.Settings
import android.support.multidex.MultiDexApplication
import android.widget.Toast
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.message.Message
import com.dj.im.sdk.listener.IImListener


/**
 * Create by ChenLei on 2020/4/11
 * Describe:
 */
class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        DJIM.addImListener(object : IImListener() {
            override fun onLogin(code: Int, message: String) {
                if (code != ResultEnum.Success.code) {
                    Toast.makeText(this@MyApp, message, Toast.LENGTH_SHORT).show()
                    // 回到登录页
                }
            }

            override fun onPushMessage(message: Message) {
                Toast.makeText(this@MyApp, "接收到消息：${message.data}", Toast.LENGTH_SHORT).show()
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