package com.dj.im.sdk.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

/**
 * Create by ChenLei on 2020/4/16
 * Describe: SharedPreferences工具类
 */
internal object SpUtil {

    private const val spFile = "djim"

    fun getSp(context: Context): SharedPreferences =
        context.getSharedPreferences(spFile, Activity.MODE_PRIVATE)
}
