package com.dj.im.sdk.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.entity.message.Message
import com.dj.im.sdk.utils.EncryptUtil


/**
 * Create by ChenLei on 2020/4/14
 * Describe: 单聊会话
 */
internal class SingleConversation(private val mToUserId: Long) : Conversation() {

    /**
     * 修改关键的信息
     */
    override fun sendMessage(message: Message) {
        message.conversationId = getConversationId()
        message.conversationType = Constant.ConversationType.SINGLE
        message.fromId = getFromUserId()
        message.toId = mToUserId
        super.sendMessage(message)
    }

    /**
     * 生成单聊的会话id
     */
    override fun getConversationId(): String {
        return generateConversationId(getFromUserId(), mToUserId)
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