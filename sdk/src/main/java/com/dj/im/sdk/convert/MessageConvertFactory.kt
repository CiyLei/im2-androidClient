package com.dj.im.sdk.convert

import com.dj.im.sdk.task.message.Message

/**
 * Create by ChenLei on 2020/4/19
 * Describe: 文字消息类型转换工厂
 */
object MessageConvertFactory {

    // 消息转换工厂
    val messageConverts =
        arrayListOf<IMessageConvert<out Message>>(TextMessageConvert())

    /**
     * 将消息转换我对应的类型
     */
    fun convert(message: Message): Message {
        for (messageConvert in messageConverts) {
            val convert = messageConvert.convert(message)
            if (convert != null) {
                return convert
            }
        }
        return message
    }
}