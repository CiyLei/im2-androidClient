package com.dj.im.sdk.convert.conversation

import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.entity.ImConversation

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 会话转换工厂
 */
object ConversationConvertFactory {

    val conversationConverts = arrayListOf(SingleConversationConvert(), GroupConversationConvert())

    fun convert(conversation: ImConversation): Conversation? {
        for (conversationConvert in conversationConverts) {
            val convert = conversationConvert.convert(conversation)
            if (convert != null) {
                return convert
            }
        }
        return null
    }
}