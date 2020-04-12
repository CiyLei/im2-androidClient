package com.dj.im

import android.app.Application
import com.dj.im.sdk.DJIM


/**
 * Create by ChenLei on 2020/4/11
 * Describe:
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DJIM.init(
            this,
            "e97f8917-272a-4ea2-a411-b66a33644368",
            "1fda7a5e-beb6-4ea8-9cee-918c71b0a8e5",
            "22222222222222222222222222222222222222222222222222222222222222222"
        )
    }
}