package com.dj.im.sdk.conversation

import android.os.Handler
import android.os.Looper
import com.dj.im.sdk.Constant
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.convert.message.MessageConvertFactory
import com.dj.im.sdk.convert.send.SendMessageTaskFactory
import com.dj.im.sdk.entity.HttpImMessage
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.UnReadMessage
import com.dj.im.sdk.listener.ImListener
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.HttpGetHistoryMessageListTask
import com.dj.im.sdk.task.ReadConversationTask
import io.reactivex.disposables.CompositeDisposable
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
        fun onUserInfoChange(userId: Long)
    }

    /**
     * 保存所有消息，以防在同步消息的时候重复
     */
    private val mHistoryMessage = ArrayList<Message>()

    /**
     * 管理请求
     */
    protected val mCompositeDisposable = CompositeDisposable()
    private val mHandler = Handler(Looper.getMainLooper())

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

        override fun onChangeMessageSendState(
            conversationKey: String,
            messageId: Long,
            state: Int
        ) {
            // 判断更新状态的消息是不是当前会话的
            if (conversationKey == getConversationKey()) {
                val index = mHistoryMessage.indexOfFirst { it.imMessage.id == messageId }
                if (index >= 0) {
                    mHistoryMessage[index].imMessage.state = state
                }
                conversationListener?.onChaneMessageState(messageId, state)
            }
        }

        override fun onChangeConversationRead(conversationKey: String) {
            if (conversationKey == getConversationKey()) {
                // 如果是自己的会话被对方已读，更新回调
                conversationListener?.onConversationRead()
            }
        }

        override fun onUserInfoChange(userId: Long) {
            conversationListener?.onUserInfoChange(userId)
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
        message.imMessage.id = Random.nextLong()
        message.imMessage.state = ImMessage.State.LOADING
        // 在发送任务的工厂中找到真正的发送任务类
        val sendMessageTask = SendMessageTaskFactory.matchMessageTask(message)
        if (sendMessageTask != null) {
            // 开始发送
            sendMessageTask.startSend()
            // 虽然这时候id是随机虚假的，这个未读的列表关联的是假的id，但是这条消息并不会保存
            // 之后发生成功了，才会保存消息，那时会在保存一次未读列表，那时会正确的关联
            addUnReadUser(sendMessageTask.getMessage())
            // 只保存到内存中
            addMessage(sendMessageTask.getMessage())
            return true
        }
        return false
    }

    /**
     * 添加消息
     */
    private fun addMessage(message: Message) {
        // 首先判断是不是此会话下面的消息
        if (message.imMessage.conversationKey == getConversationKey()) {
            synchronized(mHistoryMessage) {
                // 没有重复消息
                if (!mHistoryMessage.map { it.imMessage.id }.contains(message.imMessage.id)) {
                    mHistoryMessage.add(message)
                    conversationListener?.onPushMessage(message)
                }
            }
        }
    }

    /**
     * 获取会话Key
     */
    abstract fun getConversationKey(): String

    /**
     * 添加未读用户
     */
    abstract fun addUnReadUser(message: Message)

    /**
     * 获取当前用户id
     */
    protected fun getFromUserName(): String = ServiceManager.instance.getUserInfo()?.userName ?: ""

    /**
     * 在对应的生命周期中调用
     */
    fun onDestroy() {
        conversationListener = null
        mHistoryMessage.clear()
        mCompositeDisposable.dispose()
    }

    /**
     * 获取本地最新的20条消息
     */
    fun getLocalNewestMessages(): List<Message> {
        val result = ArrayList<Message>()
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return result
        ServiceManager.instance.getDb()?.getNewestMessages(
            ServiceManager.instance.mAppId, userName, getConversationKey(), pageSize
        )?.forEach { msg ->
            result.add(MessageConvertFactory.convert(msg))
        }
        return result
    }

    /**
     * 返回最后一条消息
     */
    fun lastMessage(): Message? {
        var lastMessage: Message? = null
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return null
        val msg = ServiceManager.instance.getDb()
            ?.getLastMessage(ServiceManager.instance.mAppId, userName, getConversationKey())
        if (msg != null) {
            lastMessage = MessageConvertFactory.convert(msg)
        }
        return lastMessage
    }

    /**
     * 已读消息
     */
    fun read() {
        unReadCount = 0
        // 更新数据库中的会话未读数量
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return
        ServiceManager.instance.getDb()?.clearConversationUnReadCount(
            ServiceManager.instance.mAppId, userName, getConversationKey()
        )
        // 通知会话更新
        ServiceManager.instance.imListeners.forEach { it.onChangeConversions() }
        // 发送会话已读消息
        ServiceManager.instance.sendTask(
            ReadConversationTask(
                getConversationKey()
            )
        )
    }

    /**
     * 获取指定消息之前的历史消息列表
     * 先从网络中获取，如果获取失败再从数据库中获取
     * @param messageId 获取指定消息之前的历史记录（0：获取最新的消息记录）
     * @param event 读取回调
     */
    fun getHistoryMessage(messageId: Long = 0, event: ((Boolean, List<Message>) -> Unit)) {
        mCompositeDisposable.add(
            HttpGetHistoryMessageListTask(getConversationKey(), messageId)
                .success {
                    // 读取历史消息成功
                    DJIM.getDefaultThreadPoolExecutor().submit {
                        // 将获取到的消息保存到本地
                        writeHistoryMessage(it)
                        notifyReadHistoryMessage(messageId, true, event)
                    }
                }.failure { _, _ ->
                    // 读取失败
                    notifyReadHistoryMessage(messageId, false, event)
                }.start()
        )
    }

    /**
     * 写入历史消息
     */
    private fun writeHistoryMessage(messageList: List<HttpImMessage>) {
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return
        messageList.forEach { msg ->
            // 添加历史消息到本地数据库
            ServiceManager.instance.getDb()?.addPushMessage(
                ServiceManager.instance.mAppId,
                userName,
                msg.toMessage(ServiceManager.instance.mAppId, userName)
            )
            // 保存未读信息
            ServiceManager.instance.getDb()?.addUnReadMessage(
                ServiceManager.instance.mAppId,
                userName,
                msg.unReadUserName.map {
                    UnReadMessage(
                        ServiceManager.instance.mAppId,
                        userName,
                        msg.id,
                        it
                    )
                })
        }
    }

    /**
     * 读取本地历史消息列表
     */
    fun getLocalHistoryMessage(messageId: Long): List<Message> {
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return emptyList()
        // 网络读取历史记录失败则从本地数据库中读取
        return ServiceManager.instance.getDb()?.getHistoryMessage(
            ServiceManager.instance.mAppId,
            userName,
            getConversationKey(),
            messageId,
            Constant.OFFLINE_READ_HISTORY_MESSAGE_COUNT
        )?.map { MessageConvertFactory.convert(it) } ?: return emptyList()
    }

    /**
     * 通知更新读取了历史消息
     */
    private fun notifyReadHistoryMessage(
        messageId: Long = 0L,
        netSuccess: Boolean,
        event: ((Boolean, List<Message>) -> Unit)
    ) {
        // 读取本地的历史消息
        val messageList = if (messageId == 0L)
            getLocalNewestMessages()
        else getLocalHistoryMessage(messageId)
        mHandler.post {
            event.invoke(netSuccess, messageList)
        }
    }
}