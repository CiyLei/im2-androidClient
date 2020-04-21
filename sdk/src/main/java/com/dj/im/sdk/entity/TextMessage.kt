package com.dj.im.sdk.entity

import com.dj.im.sdk.convert.message.Message

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 文字消息
 */
open class TextMessage(val data: String) : Message() {

    private var mMessage: ImMessage = ImMessage(data = data)

    override fun injectImMessage(message: ImMessage) {
        mMessage = message
    }

    override fun getImMessage(): ImMessage = mMessage

}