package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.ImageMessage

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 图片消息转换类
 */
class ImageMessageConvert : IMessageConvert<ImageMessage> {

    override fun convert(message: ImMessage): ImageMessage? {
        if (message.type == ImMessage.Type.IMAGE) {
            return ImageMessage(message)
        }
        return null
    }
}