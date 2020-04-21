package com.dj.im

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.conversation.SingleConversation
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.listener.ImListener
import kotlinx.android.synthetic.main.activity_conversation.*


/**
 * Create by ChenLei on 2020/4/11
 * Describe: 会话界面
 */
class ConversationActivity : BaseActivity() {

    companion object {
        val user1 = ImUser(697027057683677184L, "q903736665", "", "")
        val user2 = ImUser(697032868820094976L, "q903736668", "", "")
        val user3 = ImUser(697417018887741440L, "q903736669", "", "")
    }

    /**
     * 会话列表
     */
    private val mConversations = ArrayList<Conversation>()
    private val mAdapter = object :
        BaseQuickAdapter<Conversation, BaseViewHolder>(R.layout.item_conversation, mConversations) {

        override fun convert(helper: BaseViewHolder, item: Conversation?) {
            if (item is SingleConversation) {
                helper.setText(R.id.tvUserName, "${item.toUser.userName}(${item.toUser.id})")
                helper.setText(R.id.tvUnreadCount, item.unReadCount.toString())
                val lastMessage = item.lastMessage()
                if (lastMessage?.getImMessage()?.isRead == true || lastMessage?.getImMessage()?.fromId != DJIM.getUserInfo()?.id) {
                    helper.setText(
                        R.id.tvMessage,
                        item.lastMessage()?.getImMessage()?.getSummaryDesc()
                    )
                } else {
                    helper.setText(
                        R.id.tvMessage,
                        "[未读]${item.lastMessage()?.getImMessage()?.getSummaryDesc()}"
                    )
                }
            }
        }

    }

    private val mConversationListener = object : ImListener() {
        override fun onChangeConversions() {
            mConversations.clear()
            mConversations.addAll(DJIM.getAllConversations())
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        DJIM.getImListeners().add(mConversationListener)
        mAdapter.setOnItemClickListener { _, _, position ->
            val conversation = mConversations[position]
            if (conversation is SingleConversation) {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("user", conversation.toUser)
                })
            }
        }
        rvConversation.adapter = mAdapter

        title = DJIM.getUserInfo()?.userName
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
                    putExtra("user", user1)
                })
                return true
            }
            R.id.c2 -> {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("user", user2)
                })
                return true
            }
            R.id.c3 -> {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra("user", user3)
                })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
