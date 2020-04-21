package com.dj.im.adapter

import android.graphics.Color
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.dj.im.R
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.TextMessage


/**
 * Create by ChenLei on 2020/4/19
 * Describe:
 */
class TextMessageAdapter : BaseItemProvider<TextMessage, BaseViewHolder>() {

    override fun layout(): Int = R.layout.item_text

    override fun viewType(): Int = ImMessage.Type.TEXT

    override fun convert(helper: BaseViewHolder, data: TextMessage?, position: Int) {
        val isSelf = data?.getImMessage()?.fromId == DJIM.getUserInfo()?.id
        helper.setGone(R.id.clSelf, isSelf)
        helper.setGone(R.id.clOther, !isSelf)
        if (isSelf) {
            // 如果是自己发送的话
            helper.setText(
                R.id.rvSelfUserName,
                "${data?.getFromUser()?.userName}(${data?.getFromUser()?.id})"
            )
            helper.setText(R.id.tvSelfData, data?.getImMessage()?.data)
            helper.setText(
                R.id.tvSelfState,
                arrayOf("发送成功", "发送中", "发送失败")[data?.getImMessage()?.state ?: 0]
            )
            helper.setText(
                R.id.tvSelfIsRead,
                if (data?.getImMessage()?.isRead == true) "已读" else "未读"
            )
            helper.setTextColor(
                R.id.tvSelfIsRead,
                if (data?.getImMessage()?.isRead == true) Color.GRAY else Color.BLUE
            )
        } else {
            // 不是自己发送的
            helper.setText(
                R.id.rvOtherUserName,
                "${data?.getFromUser()?.userName}(${data?.getFromUser()?.id})"
            )
            helper.setText(R.id.tvOtherData, data?.getImMessage()?.data)
        }
    }

}