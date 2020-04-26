package com.dj.im.sdk.convert.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.conversation.SingleConversation
import com.dj.im.sdk.entity.ImConversation
import com.dj.im.sdk.service.ServiceManager

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 单聊会话转换
 */
class SingleConversationConvert : IConversationConvert {

    /**
     * 将会话对象转换为单聊会话
     */
    override fun convert(conversation: ImConversation): Conversation? {
        if (conversation.type == Constant.ConversationType.SINGLE) {
            val currentUserId = ServiceManager.instance.getUserInfo()?.id
            if (currentUserId != null) {
                val result = SingleConversation(conversation.associatedId)
                result.unReadCount = conversation.unReadCount
                return result
            }
        }
        return null
    }
}