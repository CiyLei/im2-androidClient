package com.dj.im.sdk.convert

import com.dj.im.sdk.entity.message.Message

/**
 * 消息类型转换类
 */
interface IMessageConvert<T : Message> {
    fun convert(message: Message): T?
}