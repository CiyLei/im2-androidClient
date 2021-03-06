package com.dj.im

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.conversation.GroupConversation
import com.dj.im.sdk.conversation.SingleConversation
import com.dj.im.sdk.entity.ImGroup
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.listener.ImListener
import kotlinx.android.synthetic.main.activity_conversation.*


/**
 * Create by ChenLei on 2020/4/11
 * Describe: 会话界面
 */
class ConversationActivity : BaseActivity() {

    companion object {
        val user1 = ImUser("", "", 697027057683677184L, "q903736665", "", "")
        val user2 = ImUser("", "", 697032868820094976L, "q903736668", "", "")
        val user3 = ImUser("", "", 697417018887741440L, "q903736669", "", "")
        val user4 = ImUser("", "", 62277648346255360L, "q9037366633", "", "")
        val user5 = ImUser("", "", 697417018887741440L, "q903736669", "", "")
        val group1 = ImGroup(
            "", "",
            21302854346080256L,
            "三国演义",
            "",
            listOf("q903736665", "q903736668", "q903736669")
        )
        val group2 = ImGroup(
            "", "",
            62281167189053440L,
            "两国演义",
            "",
            listOf("q903736668", "q903736669")
        )
    }

    /**
     * 会话列表
     */
    private val mConversations = ArrayList<Conversation>()
    private val mAdapter = object :
        BaseQuickAdapter<Conversation, BaseViewHolder>(R.layout.item_conversation, mConversations) {
        override fun convert(helper: BaseViewHolder, item: Conversation?) {
            val lastMessage = item?.lastMessage()
            if (item is SingleConversation) {
                if (item.getOtherSideUserInfo()?.getAvatarHttpUrl()?.isNotEmpty() == true) {
                    Glide.with(this@ConversationActivity)
                        .load(item.getOtherSideUserInfo()?.getAvatarHttpUrl())
                        .into(helper.getView(R.id.ivAvatar))
                } else {
                    helper.setImageResource(R.id.ivAvatar, R.mipmap.emoji_0x1f385)
                }
                val otherSideUserInfo = item.getOtherSideUserInfo()
                if (otherSideUserInfo == null) {
                    helper.setText(R.id.tvUserName, item.toUserName)
                } else {
                    helper.setText(
                        R.id.tvUserName,
                        "${otherSideUserInfo.alias}(${otherSideUserInfo.userName})"
                    )
                }
                helper.setText(R.id.tvUnreadCount, item.unReadCount.toString())
                if (lastMessage?.getUnReadUserIdList()?.size == 0 || lastMessage?.imMessage?.fromUserName != DJIM.getUserInfo()?.userName) {
                    helper.setText(
                        R.id.tvMessage,
                        item.lastMessage()?.imMessage?.getSummaryDesc()
                    )
                } else {
                    helper.setText(
                        R.id.tvMessage,
                        "[未读]${item.lastMessage()?.imMessage?.getSummaryDesc()}"
                    )
                }
            } else if (item is GroupConversation) {
                val groupInfo = item.getGroupInfo()
                if (groupInfo == null) {
                    helper.setText(R.id.tvUserName, "${item.groupId}")
                } else {
                    helper.setText(
                        R.id.tvUserName,
                        "${groupInfo.name}(${groupInfo.id})"
                    )
                }
                helper.setText(R.id.tvUnreadCount, item.unReadCount.toString())
                if (lastMessage?.getUnReadUserIdList()?.size == 0 || lastMessage?.imMessage?.fromUserName != DJIM.getUserInfo()?.userName) {
                    helper.setText(
                        R.id.tvMessage,
                        item.lastMessage()?.imMessage?.getSummaryDesc()
                    )
                } else {
                    helper.setText(
                        R.id.tvMessage,
                        "[${
                            lastMessage?.getUnReadUserIdList()?.size
                                ?: 0
                        }人未读]${item.lastMessage()?.imMessage?.getSummaryDesc()}"
                    )
                }
            }
            val summary = helper.getView<TextView>(R.id.tvMessage).text.toString()
            helper.setText(
                R.id.tvMessage,
                if (lastMessage?.imMessage?.revoke == true) "消息被撤销" else summary
            )
        }

    }

    private var mToken: String = ""

    private val mConversationListener = object : ImListener() {
        override fun onChangeConversions() {
            mConversations.clear()
            mConversations.addAll(DJIM.getAllConversations())
            mAdapter.notifyDataSetChanged()
        }

        override fun onUserInfoChange(userId: Long) {
            mAdapter.notifyDataSetChanged()
        }

        override fun onGroupInfoChange(groupId: Long) {
            mAdapter.notifyDataSetChanged()
        }

        override fun onLogin(code: Int, message: String) {
            if (code == ResultEnum.Success.code) {
                getSharedPreferences("djim", Activity.MODE_PRIVATE).edit()
                    .putString("token", mToken).apply()
            } else {
                startActivity(Intent(this@ConversationActivity, MainActivity::class.java))
                finish()
            }
        }

        override fun onConnect(isConnect: Boolean) {
            Log.i("ConversationActivity", "isConnect:$isConnect")
            title = "${DJIM.getUserInfo()?.alias}(${DJIM.getUserInfo()?.userName})"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        mToken = intent.getStringExtra("token") ?: ""
        if (mToken.isBlank()) {
            mToken = getSharedPreferences("djim", Activity.MODE_PRIVATE).getString("token", "")
        }
        if (mToken.isBlank()) {
            startActivity(Intent(this@ConversationActivity, MainActivity::class.java))
            finish()
        } else {
            DJIM.login(mToken)
        }

        DJIM.getImListeners().add(mConversationListener)
        mAdapter.setOnItemClickListener { _, _, position ->
            val conversation = mConversations[position]
            if (conversation is SingleConversation) {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("userName", conversation.toUserName)
                })
            } else if (conversation is GroupConversation) {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("groupId", conversation.groupId)
                })
            }
        }
        rvConversation.adapter = mAdapter

        title = "${DJIM.getUserInfo()?.alias}(${DJIM.getUserInfo()?.userName})"
        mConversations.addAll(DJIM.getAllConversations())
        mAdapter.notifyDataSetChanged()

        btnLogout.setOnClickListener { logout() }

    }

    override fun onDestroy() {
        DJIM.getImListeners().remove(mConversationListener)
        super.onDestroy()
    }

    private fun logout() {
        DJIM.logout()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.conversation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.c1 -> {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("userName", user1.userName)
                })
                return true
            }
            R.id.c2 -> {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("userName", user2.userName)
                })
                return true
            }
            R.id.c3 -> {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("userName", user3.userName)
                })
                return true
            }
            R.id.c4 -> {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("userName", user4.userName)
                })
                return true
            }
            R.id.c5 -> {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("userName", user5.userName)
                })
                return true
            }
            R.id.g1 -> {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("groupId", group1.id)
                })
                return true
            }
            R.id.g2 -> {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("groupId", group2.id)
                })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
