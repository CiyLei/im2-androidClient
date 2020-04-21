package com.dj.im.sdk.entity

import com.dj.im.sdk.convert.message.Message

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 文字消息
 */
open class TextMessage : Message {

    constructor(data: String) : super(ImMessage(data = data))
    constructor(imMessage: ImMessage) : super(imMessage)

}