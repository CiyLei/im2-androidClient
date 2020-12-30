package com.dj.im.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.dj.im.R
import com.dj.im.sdk.entity.FileMessage

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 文件消息适配器
 */
class FileViewHolder(private val mContext: Context, private val mViewGroup: ViewGroup) :
    ImMessageViewHolder<TextView, FileMessage>(mContext, mViewGroup) {

    override fun createMessageView(): TextView {
        return LayoutInflater.from(mContext)
            .inflate(R.layout.item_im_text, mViewGroup, false) as TextView
    }

    override fun onMessage(isSelf: Boolean, messageView: TextView, message: FileMessage) {
        val sb = StringBuilder("文件:${message.imMessage.data}")
        if (isSelf) {
            sb.append(" 进度:${(message.uploadProgress * 100).toInt()}%")
        }
        messageView.text = sb.toString()
        messageView.requestLayout()
    }


}