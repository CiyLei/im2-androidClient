package com.dj.im.adapter

import android.graphics.Color
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.dj.im.ChatActivity
import com.dj.im.R
import com.dj.im.sdk.entity.FileMessage
import com.dj.im.sdk.entity.ImMessage

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 文件消息适配器
 */
class FileMessageAdapter : BaseItemProvider<FileMessage, BaseViewHolder>() {

    override fun layout(): Int = R.layout.item_text

    override fun viewType(): Int = ImMessage.Type.FILE

    override fun convert(helper: BaseViewHolder, data: FileMessage?, position: Int) {
        val fromUser = data?.getFromUser()
        val isSelf = data?.isSelfSend() ?: false
        helper.setGone(R.id.clSelf, isSelf)
        helper.setGone(R.id.clOther, !isSelf)
        if (isSelf) {
            // 如果是自己发送的话
            if (fromUser == null) {
                helper.setText(R.id.rvSelfUserName, "${data?.imMessage?.fromUserName}")
            } else {
                helper.setText(
                    R.id.rvSelfUserName,
                    "${data.getFromUser()?.alias}(${data?.getFromUser()?.userName})"
                )
            }
            helper.setText(
                R.id.tvSelfData,
                "假装自己是文件 进度:${data?.uploadProgress} data:${data?.imMessage?.data}"
            )
            helper.setText(
                R.id.tvSelfState,
                arrayOf("发送成功", "发送中", "发送失败")[data?.imMessage?.state ?: 0]
            )
            if (ChatActivity.isSingle) {
                helper.setText(
                    R.id.tvSelfIsRead,
                    if (data?.getUnReadUserIdList()?.size == 0) "已读" else "未读"
                )
            } else {
                helper.setText(
                    R.id.tvSelfIsRead,
                    if (data?.getUnReadUserIdList()?.size == 0) "全部已读" else "未读[${
                    data?.getUnReadUserIdList()?.joinToString(",")}]"
                )
            }
            helper.setTextColor(
                R.id.tvSelfIsRead,
                if (data?.getUnReadUserIdList()?.size == 0) Color.GRAY else Color.BLUE
            )
        } else {
            // 不是自己发送的
            if (fromUser == null) {
                helper.setText(R.id.rvOtherUserName, "${data?.imMessage?.fromUserName}")
            } else {
                helper.setText(
                    R.id.rvOtherUserName,
                    "${data.getFromUser()?.alias}(${data.getFromUser()?.userName})"
                )
            }
            helper.setText(R.id.tvOtherData, "假装自己是文件:${data?.imMessage?.data}")
        }
    }

}