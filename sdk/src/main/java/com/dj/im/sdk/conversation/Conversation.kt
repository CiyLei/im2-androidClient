package com.dj.im.sdk.conversation

import com.dj.im.sdk.Constant
import com.dj.im.sdk.entity.message.Message
import com.dj.im.sdk.service.ServiceManager

/**
 * Create by ChenLei on 2020/4/14
 * Describe: 会话抽象
 */
abstract class Conversation {

    companion object {
        // 每次获取的条数
        private const val pageSize = 20
    }

    /**
     * 未读数量
     */
    var unReadCount = 0

    /**
     * 发送消息
     */
    open fun sendMessage(message: Message) {
        // 修改状态为发送中
        message.isRead = true
        message.state = Constant.MessageSendState.LOADING
        ServiceManager.instance.sendMessage(message)
    }

    /**
     * 获取会话id
     */
    abstract fun getConversationId(): String

    /**
     * 获取当前用户id
     */
    protected fun getFromUserId(): Long = ServiceManager.instance.getUserId() ?: 0

    /**
     * 从数据库中获取指定消息id之前的20条信息
     * @param messageId 消息id
     */
//    protected fun getOldMessagesForDB(messageId: Long): List<Message2> {
//        return emptyList()
//    }

    /**
     * 从服务器获取指定消息id之前的20条信息
     * @param messageId 消息id
     */
//    protected fun getOldMessagesForNet(messageId: Long): List<Message2> {
//        return emptyList()
//    }

    /**
     * 智能消息监听器
     * 首先从数据库中读取消息触发回调，同时从服务器中获取，如果有不同则触发回调
     */
//    fun setSmartMessageListener(listener: ((messages: List<Message2>) -> Unit)?) {
//
//    }

    /**
     * 智能获取旧消息
     * 同时从数据库和服务器中获取，双方进行等待，网络获取失败则返回数据库的结果，成功则返回服务器获取的结果
     * @return 如果在获取中又触发，则返回false获取失败
     */
    fun smartGetOldMessage(): Boolean {
        return false
    }

    /**
     * 返回最后一条消息
     */
    fun lastMessage(): Message? {
        return null
    }
}