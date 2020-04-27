package com.dj.im.sdk.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImGroup
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.GetGroupInfoTask
import com.dj.im.sdk.utils.EncryptUtil

/**
 * Create by ChenLei on 2020/4/27
 * Describe: 群聊会话
 */
class GroupConversation(val groupId: Long) : Conversation() {

    override fun sendMessage(message: Message): Boolean {
        message.imMessage.conversationKey = getConversationKey()
        message.imMessage.conversationType = Constant.ConversationType.GROUP
        message.imMessage.fromId = getFromUserId()
        message.imMessage.toId = groupId
        return super.sendMessage(message)
    }

    override fun getConversationKey(): String = EncryptUtil.MD5(groupId.toString())

    /**
     * 获取群信息
     */
    fun getGroupInfo(): ImGroup? {
        ServiceManager.instance.getUserInfo()?.let {
            val groupInfo = ServiceManager.instance.getDb()?.getGroupInfo(it.id, groupId)
            if (groupInfo == null) {
                ServiceManager.instance.sendTask(GetGroupInfoTask(groupId))
            }
            return groupInfo
        }
        return null
    }
}