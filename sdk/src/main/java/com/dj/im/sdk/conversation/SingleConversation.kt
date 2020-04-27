package com.dj.im.sdk.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.GetUserInfoTask
import com.dj.im.sdk.utils.EncryptUtil


/**
 * Create by ChenLei on 2020/4/14
 * Describe: 单聊会话
 */
class SingleConversation(val toUserId: Long) : Conversation() {

    /**
     * 修改关键的信息
     */
    override fun sendMessage(message: Message): Boolean {
        message.imMessage.conversationKey = getConversationKey()
        message.imMessage.conversationType = Constant.ConversationType.SINGLE
        message.imMessage.fromId = getFromUserId()
        message.imMessage.toId = toUserId
        return super.sendMessage(message)
    }

    /**
     * 生成单聊的会话id
     */
    override fun getConversationKey(): String {
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

    /**
     * 获取对方用户信息
     */
    fun getOtherSideUserInfo(): ImUser? {
        ServiceManager.instance.getUserInfo()?.let {
            val user = ServiceManager.instance.getDb()?.getUser(it.id, toUserId)
            if (user == null) {
                ServiceManager.instance.sendTask(GetUserInfoTask(toUserId))
            }
            return user
        }
        return null
    }
}