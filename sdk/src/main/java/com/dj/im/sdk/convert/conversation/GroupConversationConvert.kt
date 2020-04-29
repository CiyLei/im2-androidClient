package com.dj.im.sdk.convert.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.conversation.GroupConversation
import com.dj.im.sdk.entity.ImConversation
import com.dj.im.sdk.service.ServiceManager

/**
 * Create by ChenLei on 2020/4/27
 * Describe: 群聊会话转换
 */
class GroupConversationConvert : IConversationConvert {

    override fun convert(conversation: ImConversation): Conversation? {
        if (conversation.type == Constant.ConversationType.GROUP) {
            val currentUserId = ServiceManager.instance.getUserInfo()?.id
            if (currentUserId != null) {
                val result = GroupConversation(conversation.associatedId)
                result.unReadCount = conversation.unReadCount
                return result
            }
        }
        return null
    }
}