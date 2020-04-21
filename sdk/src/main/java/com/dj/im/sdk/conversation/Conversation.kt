package com.dj.im.sdk.conversation

import com.dj.im.sdk.convert.send.SendMessageTaskFactory
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.convert.message.MessageConvertFactory
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.listener.ImListener
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.HistoryMessageTask
import com.dj.im.sdk.task.ReadConversationTask
import kotlin.random.Random

/**
 * Create by ChenLei on 2020/4/14
 * Describe: 会话抽象
 */
abstract class Conversation {

    companion object {
        // 每次获取的条数
        const val pageSize = 20
    }

    /**
     * 会话的回调
     */
    interface ConversationListener {
        fun onPushMessage(message: Message)
        fun onChaneMessageState(messageId: Long, state: Int)
        fun onConversationRead()
        fun onReadHistoryMessage(messageList: List<Message>)
    }

    /**
     * 保存所有消息，以防在同步消息的时候重复
     */
    private val mHistoryMessage = HashSet<Message>()

    /**
     * 最后一条消息
     */
    private var mLastMessage: Message? = null

    /**
     * 会话处理消息后的回调
     */
    var conversationListener: ConversationListener? = null
        set(value) {
            field = value
            if (value == null) {
                ServiceManager.instance.imListeners.remove(mImListener)
            } else {
                ServiceManager.instance.imListeners.add(mImListener)
            }
        }

    /**
     * 监听全局的消息回调
     */
    private val mImListener = object : ImListener() {
        override fun onPushMessage(message: Message) {
            addMessage(message)
        }

        override fun onChangeMessageSendState(messageId: Long, state: Int) {
            // 判断更新状态的消息是不是当前会话的
            val index = mHistoryMessage.map { it.getImMessage().id }.indexOf(messageId)
            if (index >= 0) {
                // 更改消息的发送状态
                for (message in mHistoryMessage) {
                    if (message.getImMessage().id == messageId) {
                        message.getImMessage().state = state
                    }
                }
                conversationListener?.onChaneMessageState(messageId, state)
            }
        }

        override fun onChangeConversationRead(conversationId: String) {
            if (conversationId == getConversationId()) {
                // 如果是自己的会话被对方已读，更新回调
                mHistoryMessage.forEach {
                    it.getImMessage().isRead = true
                }
                conversationListener?.onConversationRead()
            }
        }

        override fun onReadHistoryMessage(conversationId: String, messageList: List<Message>) {
            if (conversationId == getConversationId()) {
                conversationListener?.onReadHistoryMessage(messageList.map { it })
            }
        }
    }

    /**
     * 未读数量
     */
    var unReadCount = 0

    /**
     * 发送消息
     */
    open fun sendMessage(message: Message): Boolean {
        // 修改状态为发送中，随便指定一个id，发送成功会话会更正为服务器的id，否则就是这个随机的id
        message.getImMessage().id = Random.nextLong()
        message.getImMessage().state = ImMessage.State.LOADING
        // 如果是自己给自己发送消息，默认已读
        if (message.getImMessage().fromId == message.getImMessage().toId) {
            message.getImMessage().isRead = true
        }
        // 在发送任务的工厂中找到真正的发送任务类
        val sendMessage = SendMessageTaskFactory.sendMessageTask(message)
        if (sendMessage != null) {
            addMessage(message)
            return true
        }
        return false
    }

    /**
     * 添加消息
     */
    private fun addMessage(message: Message) {
        // 首先判断是不是此会话下面的消息
        if (message.getImMessage().conversationId == getConversationId()) {
            synchronized(mHistoryMessage) {
                // 没有重复消息
                if (!mHistoryMessage.map {
                        it.getImMessage().id
                    }.contains(message.getImMessage().id)) {
                    mHistoryMessage.add(message)
                    conversationListener?.onPushMessage(message)
                }
            }
        }
    }

    /**
     * 获取会话id
     */
    abstract fun getConversationId(): String

    /**
     * 获取当前用户id
     */
    protected fun getFromUserId(): Long = ServiceManager.instance.getUserInfo()?.id ?: 0

    /**
     * 在对应的生命周期中调用
     */
    fun onDestroy() {
        conversationListener = null
        mHistoryMessage.clear()
    }

    /**
     * 获取最新的20条消息
     */
    fun getNewestMessages(): List<Message> {
        val result = ArrayList<Message>()
        ServiceManager.instance.getUserInfo()?.id?.let {
            ServiceManager.instance.getDb()?.getNewestMessages(
                it,
                getConversationId(),
                pageSize
            )?.forEach { msg ->
                result.add(MessageConvertFactory.convert(msg))
            }
        }
        mHistoryMessage.addAll(result)
        return result
    }

    /**
     * 返回最后一条消息
     */
    fun lastMessage(): Message? {
        if (mLastMessage == null) {
            ServiceManager.instance.getUserInfo()?.id?.let {
                val lastMessage =
                    ServiceManager.instance.getDb()?.getLastMessage(it, getConversationId())
                if (lastMessage != null) {
                    mLastMessage = MessageConvertFactory.convert(lastMessage)
                }
            }
        }
        return mLastMessage
    }

    /**
     * 已读消息
     */
    fun read() {
        unReadCount = 0
        // 更新数据库中的会话未读数量
        ServiceManager.instance.getUserInfo()?.id?.let {
            ServiceManager.instance.getDb()?.clearConversationUnReadCount(
                it,
                getConversationId()
            )
        }
        // 通知会话更新
        ServiceManager.instance.imListeners.forEach { it.onChangeConversions() }
        // 发送会话已读消息
        ServiceManager.instance.sendTask(
            ReadConversationTask(
                getConversationId()
            )
        )
    }

    /**
     * 获取指定消息之前的历史消息列表
     * 先从网络中获取，如果获取失败再从数据库中获取
     */
    fun getHistoryMessage(messageId: Long) {
        ServiceManager.instance.sendTask(
            HistoryMessageTask(
                getConversationId(),
                messageId
            )
        )
    }
}