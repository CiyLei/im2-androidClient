package com.dj.im.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.dj.im.R
import com.dj.im.sdk.entity.BigTextMessage

/**
 * Create by ChenLei on 2020/4/22
 * Describe: 大文本适配器
 */
class BigTextViewHolder(private val mContext: Context, private val mViewGroup: ViewGroup) :
    ImMessageViewHolder<TextView, BigTextMessage>(mContext, mViewGroup) {

    override fun createMessageView(): TextView {
        return LayoutInflater.from(mContext)
            .inflate(R.layout.item_im_text, mViewGroup, false) as TextView
    }

    override fun onMessage(isSelf: Boolean, messageView: TextView, message: BigTextMessage) {
        messageView.text = "大文件，描述：${message.imMessage.summary}"
        messageView.requestLayout()
    }


}