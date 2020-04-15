package com.dj.im.sdk.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.message.Message
import com.dj.im.sdk.message.SendMessage
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.utils.EncryptUtil


/**
 * Create by ChenLei on 2020/4/14
 * Describe: 单聊会话
 */
internal class SingleConversation(val toUserId: Long) : Conversation() {

    override fun convertMessage(message: Message): SendMessage.SendMessageRequest {
        return SendMessage.SendMessageRequest.newBuilder()
            .setConversationId(getConversationId())
            .setConversationType(Constant.ConversationType.SINGLE)
            .setFromId(getFromUserId()).setToId(toUserId).setType(message.type)
            .setData(message.data).setSummary(message.summary).build()
    }

    /**
     * 生成单聊的会话id
     */
    override fun getConversationId(): String {
        return generateConversationId(getFromUserId(), toUserId)
    }

    /**
     * 生成两个用户的会话id
     *
     * @return 会话id
     */
    private fun generateConversationId(user1Id: Long, user2Id: Long): String {
        return if (user1Id < user2Id) {
            EncryptUtil.MD5(user1Id.toString() + "_" + user2Id)
        } else EncryptUtil.MD5(user2Id.toString() + "_" + user1Id)
    }
}