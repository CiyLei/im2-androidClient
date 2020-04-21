package com.dj.im.sdk.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.utils.EncryptUtil


/**
 * Create by ChenLei on 2020/4/14
 * Describe: 单聊会话
 */
class SingleConversation(val toUser: ImUser) : Conversation() {

    /**
     * 修改关键的信息
     */
    override fun sendMessage(message: Message): Boolean {
        message.imMessage.conversationId = getConversationId()
        message.imMessage.conversationType = Constant.ConversationType.SINGLE
        message.imMessage.fromId = getFromUserId()
        message.imMessage.toId = toUser.id
        // 保存接收者的用户消息
        ServiceManager.instance.getUserInfo()?.id?.let {
            ServiceManager.instance.getDb()?.addUser(it, toUser)
        }
        return super.sendMessage(message)
    }

    /**
     * 生成单聊的会话id
     */
    override fun getConversationId(): String {
        return generateConversationId(getFromUserId(), toUser.id)
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