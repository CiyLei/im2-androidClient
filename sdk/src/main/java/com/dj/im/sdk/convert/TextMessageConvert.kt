package com.dj.im.sdk.convert

import com.dj.im.sdk.Constant
import com.dj.im.sdk.entity.message.Message
import com.dj.im.sdk.entity.message.TextMessage


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 文字消息类型转换
 */
class TextMessageConvert : IMessageConvert<TextMessage> {

    override fun convert(message: Message): TextMessage? {
        if (message.type == Constant.MessageType.TEXT) {
            val textMessage = TextMessage(message.data)
            textMessage.id = message.id
            textMessage.conversationId = message.conversationId
            textMessage.conversationType = message.conversationType
            textMessage.fromId = message.fromId
            textMessage.toId = message.toId
            textMessage.type = message.type
            textMessage.summary = message.summary
            textMessage.createTime = message.createTime
            textMessage.state = message.state
            textMessage.isRead = message.isRead
            return textMessage
        }
        return null
    }
}
