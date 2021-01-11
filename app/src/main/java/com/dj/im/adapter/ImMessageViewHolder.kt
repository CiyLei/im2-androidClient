package com.dj.im.adapter

import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dj.im.R
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImMessage

/**
 * Create by ChenLei on 2020/12/29
 * Describe: im消息
 */
abstract class ImMessageViewHolder<V : View, M : Message>(
    private val mContext: Context,
    viewGroup: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(mContext).inflate(R.layout.item_im_message, viewGroup, false)
) {

    val clLayout = itemView.findViewById<ConstraintLayout>(R.id.clLayout)
    val tvUserName = itemView.findViewById<TextView>(R.id.tvUserName)
    val ivAvatar = itemView.findViewById<ImageView>(R.id.ivAvatar)
    val tvIsRead = itemView.findViewById<TextView>(R.id.tvIsRead)
    val tvState = itemView.findViewById<TextView>(R.id.tvState)
    val tvRevoke = itemView.findViewById<TextView>(R.id.tvRevoke)
    val llMessage = itemView.findViewById<LinearLayout>(R.id.llMessage)
    val flMessage = itemView.findViewById<FrameLayout>(R.id.flMessage)
    lateinit var messageView: V

    fun init() {
        messageView = createMessageView()
        flMessage.addView(messageView)
    }

    /**
     * 创建消息View
     */
    protected abstract fun createMessageView(): V

    fun setMessage(message: M) {
        val self = isSelf(message)
        adjustLayout(self)
        tvUserName.text =
            message.getFromUser()?.alias ?: message.imMessage.fromUserName
        tvIsRead.visibility =
            if (self && message.imMessage.state == ImMessage.State.SUCCESS && !message.imMessage.revoke)
                View.VISIBLE
            else
                View.INVISIBLE
        tvIsRead.text = if (message.getUnReadUserIdList().isEmpty()) "已读" else "未读"
        tvIsRead.setTextColor(
            if (message.getUnReadUserIdList().isEmpty()) Color.GRAY else Color.BLUE
        )
        tvState.text = arrayOf("发送成功", "发送中", "发送失败")[message.imMessage.state]
        flMessage.setBackgroundResource(if (self) R.drawable.bg_chat_self else R.drawable.bg_chat_other)
        loadAvatar(message.getFromUser()?.getAvatarHttpUrl(), ivAvatar)
        tvRevoke.visibility = if (message.imMessage.revoke) View.VISIBLE else View.GONE
        flMessage.visibility = if (message.imMessage.revoke) View.GONE else View.VISIBLE
        if (message.imMessage.revoke) {
            // 消息已撤回
            tvRevoke.gravity = if (self) Gravity.END else Gravity.START
        } else {
            onMessage(self, messageView, message)
        }
    }

    private fun isSelf(message: M): Boolean =
        message.imMessage.fromUserName == DJIM.getUserInfo()?.userName

    protected abstract fun onMessage(isSelf: Boolean, messageView: V, message: M)

    /**
     * 加载头像
     */
    private fun loadAvatar(url: String?, imageView: ImageView) {
        if (url?.isNotBlank() == true) {
            Glide.with(mContext).load(url).into(imageView)
        }
    }

    /**
     * 调整布局
     */
    private fun adjustLayout(isSelf: Boolean) {
        if (isSelf) {
            tvState.visibility = View.VISIBLE
            tvIsRead.visibility = View.VISIBLE
            ivAvatar.layoutParams = (ivAvatar.layoutParams as ConstraintLayout.LayoutParams).apply {
                rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                leftToLeft = ConstraintLayout.LayoutParams.UNSET
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
            tvUserName.gravity = Gravity.END
            tvUserName.layoutParams =
                (tvUserName.layoutParams as ConstraintLayout.LayoutParams).apply {
                    leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToLeft = ivAvatar.id
                    leftToRight = ConstraintLayout.LayoutParams.UNSET
                    rightToRight = ConstraintLayout.LayoutParams.UNSET
                }
            llMessage.gravity = Gravity.END
            llMessage.layoutParams =
                (llMessage.layoutParams as ConstraintLayout.LayoutParams).apply {
                    leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToLeft = ivAvatar.id
                    topToBottom = tvUserName.id
                    rightToRight = ConstraintLayout.LayoutParams.UNSET
                    leftToRight = ConstraintLayout.LayoutParams.UNSET
                }
            tvState.layoutParams = (tvState.layoutParams as ConstraintLayout.LayoutParams).apply {
                rightToRight = tvUserName.id
                topToBottom = llMessage.id
            }
            tvIsRead.layoutParams = (tvIsRead.layoutParams as ConstraintLayout.LayoutParams).apply {
                rightToLeft = tvState.id
                topToTop = tvState.id
            }
        } else {
            tvState.visibility = View.GONE
            tvIsRead.visibility = View.GONE
            ivAvatar.layoutParams = (ivAvatar.layoutParams as ConstraintLayout.LayoutParams).apply {
                rightToRight = ConstraintLayout.LayoutParams.UNSET
                leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
            tvUserName.gravity = Gravity.START
            tvUserName.layoutParams =
                (tvUserName.layoutParams as ConstraintLayout.LayoutParams).apply {
                    leftToLeft = ConstraintLayout.LayoutParams.UNSET
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToLeft = ConstraintLayout.LayoutParams.UNSET
                    leftToRight = ivAvatar.id
                    rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                }
            llMessage.gravity = Gravity.START
            llMessage.layoutParams =
                (llMessage.layoutParams as ConstraintLayout.LayoutParams).apply {
                    leftToLeft = ConstraintLayout.LayoutParams.UNSET
                    rightToLeft = ConstraintLayout.LayoutParams.UNSET
                    topToBottom = tvUserName.id
                    rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    leftToRight = ivAvatar.id
                }
            tvState.layoutParams = (tvState.layoutParams as ConstraintLayout.LayoutParams).apply {
                rightToRight = ConstraintLayout.LayoutParams.UNSET
                topToBottom = ConstraintLayout.LayoutParams.UNSET
            }
            tvIsRead.layoutParams = (tvIsRead.layoutParams as ConstraintLayout.LayoutParams).apply {
                rightToLeft = ConstraintLayout.LayoutParams.UNSET
                topToTop = ConstraintLayout.LayoutParams.UNSET
            }
        }
    }
}