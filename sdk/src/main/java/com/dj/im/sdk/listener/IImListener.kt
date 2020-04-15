package com.dj.im.sdk.listener

import com.dj.im.sdk.entity.ImMessage


/**
 * Create by ChenLei on 2020/4/12
 * Describe: 监听连接情况的回调
 */
interface IImListener {
    fun onConnect(code: Int, message: String)
    fun onPushMessage(message: ImMessage)
}
