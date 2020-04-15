package com.dj.im.sdk.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.message.SendMessage
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.utils.EncryptUtil


/**
 * Create by ChenLei on 2020/4/14
 * Describe: 单聊会话
 */
internal class SingleConversation(val toUserId: Long) : IConversation {

    override fun sendMessage(message: ImMessage) {
        val sendMessageRequest =
            SendMessage.SendMessageRequest.newBuilder().setConversationId(generateConversationId())
                .setConversationType(getConversationType())
                .setFromId(getFromUserId()).setToId(toUserId).setType(message.type)
                .setData(message.data).setSummary(message.summary).build()
        ServiceManager.instance.sendMessage(
            Constant.CMD.SEND_MESSAGE,
            sendMessageRequest.toByteArray()
        )
    }


    override fun generateConversationId(): String {
        return generateConversationId(getFromUserId(), toUserId)
    }

    override fun getConversationType(): Int = Constant.ConversationType.SINGLE

    private fun getFromUserId(): Long = ServiceManager.instance.getUserId() ?: 0

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