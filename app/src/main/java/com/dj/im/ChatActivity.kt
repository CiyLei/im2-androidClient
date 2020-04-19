package com.dj.im

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.model.FileItem
import com.dj.im.adapter.MessageAdapter
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.entity.User
import com.dj.im.sdk.entity.message.Message
import com.dj.im.sdk.entity.message.TextMessage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlin.properties.Delegates

/**
 * Create by ChenLei on 2020/4/14
 * Describe:
 */
class ChatActivity : BaseActivity() {

    private var mUser by Delegates.notNull<User>()
    private lateinit var mConversation: Conversation

    // 消息列表
    private val mMessageList = ArrayList<Message>()
    private val mAdapter = MessageAdapter(mMessageList)

    // 监听会话的消息
    private val mConversationListener = object : Conversation.ConversationListener {
        override fun onPushMessage(message: Message) {
            mMessageList.add(0, message)
            mAdapter.notifyItemInserted(0)
            rvMessageList.scrollToPosition(0)
        }

        override fun onChaneMessageState(messageId: Long, state: Int) {
            val index = mMessageList.map { it.id }.indexOf(messageId)
            mAdapter.notifyItemChanged(index)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mUser = intent.getSerializableExtra("user") as User
        title = mUser.userName
        // 获取会话对象
        mConversation = DJIM.getSingleConversation(mUser)
        // 添加最新的消息列表
        mMessageList.addAll(mConversation.getNewestMessages())
        // 设置会话的回调
        mConversation.conversationListener = mConversationListener

        rvMessageList.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true //列表再底部开始展示，反转后由上面开始展示
            reverseLayout = true //列表翻转
            scrollToPositionWithOffset(0, Int.MIN_VALUE)
        }
        rvMessageList.adapter = mAdapter

        chat_input.setMenuClickListener(object : OnMenuClickListener {
            override fun switchToMicrophoneMode(): Boolean = true

            override fun switchToEmojiMode(): Boolean = true

            override fun switchToCameraMode(): Boolean = true

            override fun switchToGalleryMode(): Boolean = true

            override fun onSendTextMessage(input: CharSequence?): Boolean {
                // 发送消息
                mConversation.sendMessage(TextMessage(input.toString()))
                return true
            }

            override fun onSendFiles(list: MutableList<FileItem>?) {

            }
        })
    }

    override fun onDestroy() {
        // 会话释放资源
        mConversation.onDestroy()
        super.onDestroy()
    }
}