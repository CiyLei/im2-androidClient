package com.dj.im

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener
import cn.jiguang.imui.chatinput.model.FileItem
import com.dj.im.adapter.MessageAdapter
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.conversation.GroupConversation
import com.dj.im.sdk.conversation.SingleConversation
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.File
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


/**
 * Create by ChenLei on 2020/4/14
 * Describe:
 */
class ChatActivity : BaseActivity() {

    companion object {
        @JvmStatic
        var isSingle = true
    }

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
            // 设置已读
            mConversation.read()
        }

        override fun onChaneMessageState(messageId: Long, state: Int) {
            val index = mMessageList.map { it.imMessage.id }.indexOf(messageId)
            mAdapter.notifyItemChanged(index)
        }

        override fun onConversationRead() {
            mAdapter.notifyDataSetChanged()
        }

        override fun onReadHistoryMessage(messageList: List<Message>) {
            mMessageList.addAll(messageList)
            mAdapter.notifyDataSetChanged()
            srl.finishRefresh()
        }

        override fun onUserInfoChange(userId: Long) {
            mAdapter.notifyDataSetChanged()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userId = intent.getLongExtra("userId", 0L)
        val groupId = intent.getLongExtra("groupId", 0L)
        isSingle = groupId == 0L
        // 获取会话对象
        if (isSingle) {
            mConversation = DJIM.getSingleConversation(userId)
            val user = (mConversation as SingleConversation).getOtherSideUserInfo()
            title = if (user == null) {
                "$userId"
            } else {
                "${user.alias}(${user.userName})"
            }
        } else {
            mConversation = DJIM.getGroupConversation(groupId)
            val groupInfo = (mConversation as GroupConversation).getGroupInfo()
            title = if (groupInfo == null) {
                "$groupId"
            } else {
                "${groupInfo.name}(${groupInfo.id})"
            }
        }
        // 添加最新的消息列表
        mMessageList.addAll(mConversation.getNewestMessages())
        // 设置会话的回调
        mConversation.conversationListener = mConversationListener
        // 已读消息
        mConversation.read()

        rvMessageList.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true //列表再底部开始展示，反转后由上面开始展示
            reverseLayout = true //列表翻转
            scrollToPositionWithOffset(0, Int.MIN_VALUE)
        }
        rvMessageList.adapter = mAdapter
        srl.setOnRefreshListener {
            // 刷新获取历史消息
            mConversation.getHistoryMessage(mMessageList.last().imMessage.id)
        }

        chat_input.setMenuClickListener(object : OnMenuClickListener {
            override fun switchToMicrophoneMode(): Boolean = true

            override fun switchToEmojiMode(): Boolean = true

            override fun switchToCameraMode(): Boolean = true

            override fun switchToGalleryMode(): Boolean = true

            override fun onSendTextMessage(input: CharSequence?): Boolean {
                if (input?.isNotBlank() == true) {
                    // 发送消息
                    mConversation.sendMessage(TextMessage(input.toString()))
                }
                return true
            }

            override fun onSendFiles(list: MutableList<FileItem>?) {
                if (list?.isNotEmpty() == true) {
                    when (list[0].type) {
                        FileItem.Type.Image -> {
                            // 发送图片消息
                            mConversation.sendMessage(ImageMessage(File(list[0].filePath)))
                        }
                    }
                }
            }
        })
        chat_input.setRecordVoiceListener(object : RecordVoiceListener {

            override fun onFinishRecord(voiceFile: File?, duration: Int) {
                if (voiceFile != null) {
                    // 发送语音消息
                    mConversation.sendMessage(VoiceMessage(voiceFile, duration * 1000))
                }
            }

            override fun onCancelRecord() {
            }

            override fun onPreviewCancel() {
            }

            override fun onPreviewSend() {
            }

            override fun onStartRecord() {
                // 设置存放录音文件目录
                val rootDir: File = filesDir
                val fileDir = rootDir.absolutePath + "/voice"
                chat_input.recordVoiceButton.setVoiceFilePath(
                    fileDir, System.currentTimeMillis().toString()
                )
            }

        })

        btnFile.setOnClickListener {
            // 选择文件
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("*/*")
            this.startActivityForResult(intent, 12345)
        }
    }

    override fun onDestroy() {
        // 会话释放资源
        mConversation.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12345 && resultCode == Activity.RESULT_OK && data != null) {
            val path = FileUtils.getPath(this, data.data)?.let {
                // 发送文件消息
                mConversation.sendMessage(FileMessage(File(it)))
            }
        }
    }
}