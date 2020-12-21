package com.dj.im.sdk.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImGroup
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.HttpGetGroupInfoTask
import com.dj.im.sdk.utils.EncryptUtil

/**
 * Create by ChenLei on 2020/4/27
 * Describe: 群聊会话
 */
class GroupConversation(val groupId: Long) : Conversation() {

    override fun sendMessage(message: Message): Boolean {
        message.imMessage.conversationKey = getConversationKey()
        message.imMessage.conversationType = Constant.ConversationType.GROUP
        message.imMessage.fromUserName = getFromUserName()
        message.imMessage.toUserName = groupId.toString()
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
            val unReadUser =
                getGroupInfo()?.userNameList?.filter { l -> it.userName != l } ?: emptyList()
            // 添加到临时的消息未读列表中
            message.imMessage.unReadUserName.clear()
            message.imMessage.unReadUserName.addAll(unReadUser)
        }
    }

    /**
     * 获取群信息
     */
    fun getGroupInfo(): ImGroup? {
        ServiceManager.instance.getUserInfo()?.let {
            val groupInfo = ServiceManager.instance.getDb()
                ?.getGroupInfo(ServiceManager.instance.mAppKey, it.userName, groupId)
            if (groupInfo == null) {
                mCompositeDisposable.add(HttpGetGroupInfoTask(listOf(groupId)).start())
            }
            return groupInfo
        }
        return null
    }
}