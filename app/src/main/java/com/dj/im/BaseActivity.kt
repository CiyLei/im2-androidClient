package com.dj.im

import android.support.v7.app.AppCompatActivity
import com.dj.im.sdk.DJIM


/**
 * Create by ChenLei on 2020/4/12
 * Describe:
 */
open class BaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        DJIM.onForeground(true)
    }

    override fun onPause() {
        super.onPause()
        super.onResume()
        DJIM.onForeground(false)
    }
}