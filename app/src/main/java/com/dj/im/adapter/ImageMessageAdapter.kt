package com.dj.im.adapter

import android.graphics.Color
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.dj.im.R
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.ImageMessage

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 图片消息适配器
 */
class ImageMessageAdapter : BaseItemProvider<ImageMessage, BaseViewHolder>() {

    private val mImagePrefix = "http://192.168.1.101:8081/download/"

    override fun layout(): Int = R.layout.item_image

    override fun viewType(): Int = ImMessage.Type.IMAGE

    override fun convert(helper: BaseViewHolder, data: ImageMessage?, position: Int) {
        val isSelf = data?.imMessage?.fromId == DJIM.getUserInfo()?.id
        helper.setGone(R.id.clSelf, isSelf)
        helper.setGone(R.id.clOther, !isSelf)
        if (isSelf) {
            // 如果是自己发送的话
            helper.setText(
                R.id.rvSelfUserName,
                "${data?.getFromUser()?.alias}(${data?.getFromUser()?.userName})"
            )
            val iv = helper.getView<ImageView>(R.id.ivSelfData)
            if (data?.fileEntity?.localPath?.isNotBlank() == true) {
                Glide.with(mContext).load(data.fileEntity.localPath).into(iv)
            } else {
                Glide.with(mContext)
                    .load("$mImagePrefix${data?.fileEntity?.netResId}")
                    .into(iv)
            }
            helper.setText(
                R.id.tvSelfState,
                arrayOf("发送成功", "发送中", "发送失败")[data?.imMessage?.state ?: 0]
            )
            helper.setText(
                R.id.tvSelfIsRead,
                if (data?.imMessage?.isRead == true) "已读" else "未读"
            )
            helper.setTextColor(
                R.id.tvSelfIsRead,
                if (data?.imMessage?.isRead == true) Color.GRAY else Color.BLUE
            )
        } else {
            // 不是自己发送的
            helper.setText(
                R.id.rvOtherUserName,
                "${data?.getFromUser()?.alias}(${data?.getFromUser()?.userName})"
            )
            val iv = helper.getView<ImageView>(R.id.ivOtherData)
            if (data?.fileEntity?.localPath?.isNotBlank() == true) {
                Glide.with(mContext).load(data.fileEntity.localPath).into(iv)
            } else {
                val url = "$mImagePrefix${data?.fileEntity?.netResId}"
                Glide.with(mContext)
                    .load(url)
                    .into(iv)
            }
        }
    }
}