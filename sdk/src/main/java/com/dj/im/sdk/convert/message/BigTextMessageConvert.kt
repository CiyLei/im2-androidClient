package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.BigTextMessage
import com.dj.im.sdk.entity.ImMessage

/**
 * Create by ChenLei on 2020/4/22
 * Describe: 大文本消息转换
 */
class BigTextMessageConvert : IMessageConvert<BigTextMessage> {

    override fun convert(message: ImMessage): BigTextMessage? {
        if (message.type == ImMessage.Type.BIG_TEXT) {
            return BigTextMessage(message)
        }
        return null
    }
}