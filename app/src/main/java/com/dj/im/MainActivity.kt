package com.dj.im

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.ResultEnum
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogin.setOnClickListener { login() }
    }

    private fun login() {
        DJIM.login(etToken.text.toString()) { code, message ->
            if (code == ResultEnum.Success.code) {
                Toast.makeText(
                    this@MainActivity,
                    "登录用户：${DJIM.getUserName()}",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@MainActivity, ConversationActivity::class.java))
            } else {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
