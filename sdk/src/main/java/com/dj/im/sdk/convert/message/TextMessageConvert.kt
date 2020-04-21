package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.TextMessage


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 文字消息类型转换
 */
class TextMessageConvert : IMessageConvert<TextMessage> {

    override fun convert(message: ImMessage): TextMessage {
        return TextMessage(message)
    }
}
