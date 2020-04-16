package com.dj.im

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.listener.IImListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val loginListener = object : IImListener() {
        override fun onConnect(code: Int, message: String) {
            if (code == ResultEnum.Success.code) {
                Toast.makeText(
                    this@MainActivity,
                    "登录用户：${DJIM.getUserName()}",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@MainActivity, ConversationActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DJIM.addImListener(loginListener)
        btnLogin.setOnClickListener { login() }
    }

    private fun login() {
        DJIM.login(etToken.text.toString())
    }

    override fun onDestroy() {
        DJIM.removeImListener(loginListener)
        super.onDestroy()
    }
}
