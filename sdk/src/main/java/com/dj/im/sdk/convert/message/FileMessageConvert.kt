package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.FileMessage
import com.dj.im.sdk.entity.ImMessage

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 文件消息转换
 */
class FileMessageConvert : IMessageConvert<FileMessage> {

    override fun convert(message: ImMessage): FileMessage? {
        if (message.type == ImMessage.Type.FILE) {
            return FileMessage(message)
        }
        return null
    }
}