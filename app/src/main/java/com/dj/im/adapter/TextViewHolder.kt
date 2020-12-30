package com.dj.im.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.dj.im.R
import com.dj.im.sdk.entity.TextMessage

/**
 * Create by ChenLei on 2020/12/7
 * Describe: 文字ViewHolder
 */
class TextViewHolder(private val mContext: Context, private val mViewGroup: ViewGroup) :
    ImMessageViewHolder<TextView, TextMessage>(mContext, mViewGroup) {

    override fun createMessageView(): TextView {
        return LayoutInflater.from(mContext)
            .inflate(R.layout.item_im_text, mViewGroup, false) as TextView
    }

    override fun onMessage(isSelf: Boolean, messageView: TextView, message: TextMessage) {
        messageView.text = message.imMessage.data
        messageView.requestLayout()
    }

}
