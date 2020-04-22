package com.dj.im

import android.content.Intent
import android.provider.Settings
import android.support.multidex.MultiDexApplication
import android.widget.Toast
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.listener.ImListener


/**
 * Create by ChenLei on 2020/4/11
 * Describe:
 */
class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
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