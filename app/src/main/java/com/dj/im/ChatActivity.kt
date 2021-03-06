package com.dj.im

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.jiguang.imui.chatinput.emoji.EmoticonsKeyboardUtils
import cn.jiguang.imui.chatinput.listener.OnCameraCallbackListener
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener
import cn.jiguang.imui.chatinput.model.FileItem
import com.dj.im.adapter.ChatAdapter
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.conversation.GroupConversation
import com.dj.im.sdk.conversation.SingleConversation
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.FileMessage
import com.dj.im.sdk.entity.ImageMessage
import com.dj.im.sdk.entity.TextMessage
import com.dj.im.sdk.entity.VoiceMessage
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.File


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
    private val mAdapter by lazy {
        ChatAdapter(this, mMessageList, rvMessageList).apply {
            onItemLongClickListener = object : ChatAdapter.OnItemLongClickListener {
                // 长按
                override fun onItemLongClick(view: View) {
                    val position = rvMessageList.getChildAdapterPosition(view)
                    val message = mMessageList[position]
                    if (message.isSelfSend() && !message.imMessage.revoke) {
                        AlertDialog.Builder(this@ChatActivity).setTitle("撤回消息")
                            .setPositiveButton("确认") { _, _ ->
                                mConversation.revokeMessage(message.imMessage.id)
                            }.setNegativeButton("取消") { _, _ -> }.show()
                    }
                }
            }
        }
    }

    // 监听会话的消息
    private val mConversationListener = object : Conversation.ConversationListener {
        override fun onPushMessage(message: Message) {
            mMessageList.add(0, message)
            mAdapter.notifyItemInserted(0)
            // 设置已读
            mConversation.read()
        }

        override fun onChaneMessageState(messageId: Long, state: Int) {
            val index = mMessageList.indexOfFirst { it.imMessage.id == messageId }
            mAdapter.notifyItemChanged(index)
        }

        override fun onConversationRead() {
            mAdapter.notifyDataSetChanged()
        }

        override fun onUserInfoChange(userId: Long) {
            mAdapter.notifyDataSetChanged()
        }

        override fun onRevokeMessage(messageId: Long) {
            mMessageList.indexOfFirst { it.imMessage.id == messageId }.let {
                mMessageList[it].imMessage.revoke = true
                mAdapter.notifyItemChanged(it)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userName = intent.getStringExtra("userName")
        val groupId = intent.getLongExtra("groupId", 0L)
        isSingle = groupId == 0L
        // 获取会话对象
        if (isSingle) {
            mConversation = DJIM.getSingleConversation(userName)
            val user = (mConversation as SingleConversation).getOtherSideUserInfo()
            title = if (user == null) {
                userName
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
        // 设置会话的回调
        mConversation.conversationListener = mConversationListener
        // 先加载本地的记录
        mMessageList.addAll(mConversation.getLocalNewestMessages())
        // 同时读取网络的记录
        mConversation.getHistoryMessage(0) { success, list ->
            if (success) {
                // 读取成功，替代本地的消息列表
                mMessageList.clear()
                mMessageList.addAll(list)
                mAdapter.notifyDataSetChanged()
            }
        }
        // 已读消息
        mConversation.read()

        rvMessageList.onDownSlideListener = object : ImRecyclerView.OnDownSlideListener {
            override fun onDownSlide() {
                EmoticonsKeyboardUtils.closeSoftKeyboard(this@ChatActivity)
            }
        }
        rvMessageList.adapter = mAdapter
        srl.setOnRefreshListener {
            // 刷新获取历史消息
            mConversation.getHistoryMessage(mMessageList.last().imMessage.id) { success, list ->
                mMessageList.addAll(list)
                mAdapter.notifyDataSetChanged()
                srl.finishRefresh()
            }
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
                    rvMessageList.getImLayoutManager().scrollToPositionWithOffset(
                        0,
                        0
                    )
                }
                return true
            }

            override fun onSendFiles(list: MutableList<FileItem>?) {
                list?.filter { it.type == FileItem.Type.Image }?.forEach {
                    // 发送图片消息
                    mConversation.sendMessage(ImageMessage(File(it.filePath)))
                    rvMessageList.getImLayoutManager().scrollToPositionWithOffset(
                        0,
                        0
                    )
                }
            }
        })
        chat_input.setOnCameraCallbackListener(object : OnCameraCallbackListener {
            override fun onTakePictureCompleted(photoPath: String?) {
                runOnUiThread {
                    mConversation.sendMessage(ImageMessage(File(photoPath)))
                    rvMessageList.getImLayoutManager().scrollToPositionWithOffset(0, 0)
                }
            }

            override fun onStartVideoRecord() {

            }

            override fun onFinishVideoRecord(videoPath: String?) {
                runOnUiThread {
                    mConversation.sendMessage(FileMessage(File(videoPath)))
                    rvMessageList.getImLayoutManager().scrollToPositionWithOffset(0, 0)
                }
            }

            override fun onCancelVideoRecord() {

            }

        })
        chat_input.setRecordVoiceListener(object : RecordVoiceListener {

            override fun onFinishRecord(voiceFile: File?, duration: Int) {
                if (voiceFile != null) {
                    // 发送语音消息
                    mConversation.sendMessage(VoiceMessage(voiceFile, duration.toFloat()))
                    rvMessageList.getImLayoutManager().scrollToPositionWithOffset(
                        0,
                        0
                    )
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
            FileUtils.getPath(this, data.data)?.let {
                // 发送文件消息
                mConversation.sendMessage(FileMessage(File(it)))
                rvMessageList.getImLayoutManager().scrollToPositionWithOffset(
                    0,
                    0
                )
            }
        }
    }

}