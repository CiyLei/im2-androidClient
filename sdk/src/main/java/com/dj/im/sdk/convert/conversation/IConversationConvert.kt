package com.dj.im.sdk.convert.conversation

import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.entity.ImConversation

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 会话转换抽象
 */
interface IConversationConvert {
    fun convert(conversation: ImConversation): Conversation?
}