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

        btnLogout.setOnClickListener { logout() }
        btnTest.setOnClickListener {
            startActivity(
                Intent(
                    this@ConversationActivity,
                    TestActivity::class.java
                )
            )
        }
    }

    private fun logout() {
        DJIM.logout()
        finish()
    }
}
