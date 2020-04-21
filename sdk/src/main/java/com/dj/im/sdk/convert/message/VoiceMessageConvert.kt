package com.dj.im.sdk.convert.message

import com.dj.im.sdk.entity.VoiceMessage
import com.dj.im.sdk.entity.ImMessage

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 语音消息转换
 */
class VoiceMessageConvert : IMessageConvert<VoiceMessage> {

    override fun convert(message: ImMessage): VoiceMessage? {
        if (message.type == ImMessage.Type.VOICE) {
            return VoiceMessage(message)
        }
        return null
    }
}