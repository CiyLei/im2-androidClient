package com.dj.im.sdk.entity

import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.service.ServiceManager

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 文字消息
 */
open class TextMessage : Message {

    constructor(data: String) : super(
        ImMessage(
            ServiceManager.instance.mAppId,
            ServiceManager.instance.getUserInfo()?.userName ?: "",
            data = data
        )
    )

    constructor(imMessage: ImMessage) : super(imMessage)

}