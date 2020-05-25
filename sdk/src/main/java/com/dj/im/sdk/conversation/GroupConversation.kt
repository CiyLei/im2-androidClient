package com.dj.im.sdk.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImGroup
import com.dj.im.sdk.entity.UnReadMessage
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

    /**
     * 生成单聊的会话id
     */
    override fun getConversationKey(): String = EncryptUtil.MD5(groupId.toString())

    /**
     * 添加未读用户
     */
    override fun addUnReadUser(message: Message) {
        ServiceManager.instance.getUserInfo()?.let {
            // 获取群里所有用户（排除自己）
            val unReadUser = getGroupInfo()?.userIdList?.filter { l -> it.id != l } ?: emptyList()
            ServiceManager.instance.getDb()?.addUnReadMessage(
                it.id, unReadUser.map { m -> UnReadMessage(it.id, message.imMessage.id, m) }
            )
        }

    }

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