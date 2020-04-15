package com.dj.im

import android.content.Intent
import android.os.Bundle
import com.dj.im.sdk.DJIM
import kotlinx.android.synthetic.main.activity_conversation.*


/**
 * Create by ChenLei on 2020/4/11
 * Describe: 会话界面
 */
class ConversationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        title = DJIM.getUserName()

        btnLogout.setOnClickListener { logout() }
        fl1.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java).apply {
                putExtra("userName", "q903736665")
                putExtra("userId", 697027057683677184L)
            })
        }
        fl2.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java).apply {
                putExtra("userName", "q903736668")
                putExtra("userId", 697032868820094976L)
            })
        }
        fl3.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java).apply {
                putExtra("userName", "q903736669")
                putExtra("userId", 697417018887741440L)
            })
        }
    }

    private fun logout() {
        DJIM.logout()
        finish()
    }
}
