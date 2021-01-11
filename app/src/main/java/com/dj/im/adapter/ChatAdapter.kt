package com.dj.im.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.*

class ChatAdapter(
    private val mContext: Context,
    private val mData: List<Message>,
    private val mRecyclerView: RecyclerView
) : RecyclerView.Adapter<ImMessageViewHolder<*, *>>() {

    companion object {
        // 是否触摸过，如果触摸过，加载图片的时候就不会滚到底部
        var touched = false
    }

    var onItemLongClickListener: OnItemLongClickListener? = null

    init {
        // 初始化
        touched = false
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ImMessageViewHolder<*, *> {
        return when (p1) {
            ImMessage.Type.TEXT -> TextViewHolder(mContext, p0).apply { init() }
            ImMessage.Type.IMAGE -> ImageViewHolder(mContext, p0, mRecyclerView).apply { init() }
            ImMessage.Type.VOICE -> VoiceViewHolder(mContext, p0).apply { init() }
            ImMessage.Type.BIG_TEXT -> BigTextViewHolder(mContext, p0).apply { init() }
            ImMessage.Type.FILE -> FileViewHolder(mContext, p0).apply { init() }
            else -> TextViewHolder(mContext, p0).apply { init() }
        }.apply {
            itemView.setOnLongClickListener {
                onItemLongClickListener?.onItemLongClick(it)
                false
            }
        }
    }

    override fun onBindViewHolder(p0: ImMessageViewHolder<*, *>, p1: Int) {
        val message = mData[p1]
        when {
            p0 is TextViewHolder && message is TextMessage -> {
                p0.setMessage(message)
            }
            p0 is ImageViewHolder && message is ImageMessage -> {
                p0.setMessage(message)
            }
            p0 is VoiceViewHolder && message is VoiceMessage -> {
                p0.setMessage(message)
            }
            p0 is BigTextViewHolder && message is BigTextMessage -> {
                p0.setMessage(message)
            }
            p0 is FileViewHolder && message is FileMessage -> {
                p0.setMessage(message)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mData[position].imMessage.type
    }

    override fun getItemCount(): Int = mData.size

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View)
    }
}