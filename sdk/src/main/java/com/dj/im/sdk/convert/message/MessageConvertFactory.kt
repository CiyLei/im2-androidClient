package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.ImMessage

/**
 * Create by ChenLei on 2020/4/19
 * Describe: 文字消息类型转换工厂
 */
object MessageConvertFactory {

    // 消息转换工厂
    val messageConverts = arrayListOf<IMessageConvert<Message>>()

    /**
     * 将消息转换我对应的类型
     */
    fun convert(message: ImMessage): Message {
        for (messageConvert in messageConverts) {
            val convert = messageConvert.convert(message)
            if (convert != null) {
                return convert
            }
        }
        // 保底为文字消息
        return TextMessageConvert().convert(message)
    }
}