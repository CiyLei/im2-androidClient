package com.dj.im.sdk.listener

import com.dj.im.sdk.entity.message.Message


/**
 * Create by ChenLei on 2020/4/12
 * Describe: 监听连接情况的回调
 */
open class IImListener {
    open fun onLogin(code: Int, message: String) {}
    open fun onPushMessage(message: Message) {}
}
