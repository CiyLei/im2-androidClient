package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.ImMessage

/**
 * 消息类型转换类
 */
interface IMessageConvert<T : Message> {
    fun convert(message: ImMessage): T?
}