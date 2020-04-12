package com.dj.im.sdk.listener


/**
 * Create by ChenLei on 2020/4/12
 * Describe: 监听连接情况的回调
 */
interface IConnectListener {
    fun result(code: Int, message: String)
}
