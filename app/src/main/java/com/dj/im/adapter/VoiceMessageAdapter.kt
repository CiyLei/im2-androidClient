package com.dj.im.adapter

import android.graphics.Color
import android.media.MediaPlayer
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.dj.im.R
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.VoiceMessage

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 语音消息适配器
 */
class VoiceMessageAdapter : BaseItemProvider<VoiceMessage, BaseViewHolder>() {

    private val mImagePrefix = "http://192.168.2.116:8081/download/"
    private var mMediaPlayer: MediaPlayer? = null

    override fun layout(): Int = R.layout.item_voice

    override fun viewType(): Int = ImMessage.Type.VOICE

    override fun convert(helper: BaseViewHolder, data: VoiceMessage?, position: Int) {
        val isSelf = data?.imMessage?.fromId == DJIM.getUserInfo()?.id
        helper.setGone(R.id.clSelf, isSelf)
        helper.setGone(R.id.clOther, !isSelf)
        if (isSelf) {
            // 如果是自己发送的话
            helper.setText(
                R.id.rvSelfUserName,
                "${data?.getFromUser()?.alias}(${data?.getFromUser()?.userName})"
            )
            helper.setText(R.id.tvSelfData, "假装我是语音：${data?.duration}秒")
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
            helper.setText(R.id.tvOtherData, "假装我是语音：${data?.duration}秒")
        }
    }

    override fun onClick(helper: BaseViewHolder?, data: VoiceMessage?, position: Int) {
        super.onClick(helper, data, position)
        var voiceUrl: String? = null
        if (data?.fileEntity?.localPath?.isNotBlank() == true) {
            voiceUrl = data.fileEntity.localPath
        } else {
            voiceUrl = "$mImagePrefix${data?.fileEntity?.netResId}"
        }
        if (mMediaPlayer != null) {
            mMediaPlayer?.release()
            mMediaPlayer = null
        }
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setDataSource(voiceUrl)
        mMediaPlayer?.prepareAsync();
        mMediaPlayer?.setOnPreparedListener {
            mMediaPlayer?.start()
        }
    }
}