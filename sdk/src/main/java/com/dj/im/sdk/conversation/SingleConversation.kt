package com.dj.im.sdk.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.HttpGetUserInfoByNames
import com.dj.im.sdk.utils.EncryptUtil


/**
 * Create by ChenLei on 2020/4/14
 * Describe: 单聊会话
 */
class SingleConversation(val toUserName: String) : Conversation() {

    /**
     * 修改关键的信息
     */
    override fun sendMessage(message: Message): Boolean {
        message.imMessage.conversationKey = getConversationKey()
        message.imMessage.conversationType = Constant.ConversationType.SINGLE
        message.imMessage.fromUserName = getFromUserName()
        message.imMessage.toUserName = toUserName
        return super.sendMessage(message)
    }

    /**
     * 生成单聊的会话id
     */
    override fun getConversationKey(): String {
        return generateConversationId(getFromUserName(), toUserName)
    }

    /**
     * 添加未读用户
     */
    override fun addUnReadUser(message: Message) {
        ServiceManager.instance.getUserInfo()?.let {
            message.imMessage.unReadUserName.clear()
            message.imMessage.unReadUserName.add(toUserName)
        }
    }

    /**
     * 生成两个用户的会话id
     *
     * @return 会话id
     */
    private fun generateConversationId(user1Name: String, user2Name: String): String {
        val appId = ServiceManager.instance.mAppId
        return if (user1Name < user2Name) {
            EncryptUtil.MD5("${appId}_${user1Name}_$user2Name")
        } else EncryptUtil.MD5("${appId}_${user2Name}_$user1Name")
    }

    /**
     * 获取对方用户信息
     */
    fun getOtherSideUserInfo(): ImUser? {
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return null
        val user = ServiceManager.instance.getDb()
            ?.getUser(ServiceManager.instance.mAppId, userName, toUserName)
        if (user == null) {
            mCompositeDisposable.add(HttpGetUserInfoByNames(listOf(toUserName)).start())
        }
        return user
    }
}