package com.dj.im.sdk.service.handler

import com.dj.im.sdk.message.ResponseMessage


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 推送消息的处理器
 */
internal interface IPushHandler {
    fun onHandle(response: ResponseMessage.Response)
}
