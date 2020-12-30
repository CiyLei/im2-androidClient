package com.dj.im.adapter

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.dj.im.R
import com.dj.im.sdk.entity.VoiceMessage
import java.text.DecimalFormat

/**
 * Create by ChenLei on 2020/12/7
 * Describe: 语音ViewHolder
 */
class VoiceViewHolder(private val mContext: Context, private val mViewGroup: ViewGroup) :
    ImMessageViewHolder<TextView, VoiceMessage>(mContext, mViewGroup) {

    private val mDecimalFormat = DecimalFormat("0.##")
    private var mMediaPlayer: MediaPlayer? = null

    override fun createMessageView(): TextView {
        return LayoutInflater.from(mContext)
            .inflate(R.layout.item_im_voice, mViewGroup, false) as TextView
    }

    override fun onMessage(isSelf: Boolean, messageView: TextView, message: VoiceMessage) {
        messageView.text = "语音：${mDecimalFormat.format(message.duration)}秒"
        messageView.requestLayout()
        messageView.setOnClickListener {
            playVoice(message.getFileHttpUrl())
        }
    }

    /**
     * 播放录音
     */
    private fun playVoice(url: String) {
        if (mMediaPlayer != null) {
            mMediaPlayer?.release()
            mMediaPlayer = null
        }
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setDataSource(url)
        mMediaPlayer?.prepareAsync();
        mMediaPlayer?.setOnPreparedListener {
            mMediaPlayer?.start()
        }
    }

}
