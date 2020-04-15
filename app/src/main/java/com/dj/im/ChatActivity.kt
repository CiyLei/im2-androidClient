package com.dj.im

import android.os.Bundle
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.model.FileItem
import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.messages.MsgListAdapter
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.conversation.IConversation
import com.dj.im.sdk.entity.ImMessage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlin.properties.Delegates

/**
 * Create by ChenLei on 2020/4/14
 * Describe:
 */
class ChatActivity : BaseActivity() {

    var userId by Delegates.notNull<Long>()
    val adapter = MsgListAdapter<MyMessage>("0", null)
    lateinit var conversation: IConversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        title = intent.getStringExtra("userName")
        userId = intent.getLongExtra("userId", 0L)

        conversation = DJIM.getSingleConversation(userId)

        msg_list.setAdapter(adapter)
        msg_list.setShowSenderDisplayName(true);
        msg_list.setShowReceiverDisplayName(true);
        chat_input.setMenuClickListener(object : OnMenuClickListener {
            override fun switchToMicrophoneMode(): Boolean = true

            override fun switchToEmojiMode(): Boolean = true

            override fun switchToCameraMode(): Boolean = true

            override fun switchToGalleryMode(): Boolean = true

            override fun onSendTextMessage(input: CharSequence?): Boolean {
                conversation.sendMessage(ImMessage(0, input.toString(), ""))
                adapter.addToStart(
                    MyMessage(
                        DefaultUser(
                            DJIM.getUserId().toString(),
                            DJIM.getUserName()!!,
                            ""
                        ), input.toString()
                    ), true
                )
                return true
            }

            override fun onSendFiles(list: MutableList<FileItem>?) {

            }
        })
    }
}