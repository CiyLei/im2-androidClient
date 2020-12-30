package com.dj.im.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dj.im.R
import com.dj.im.sdk.entity.ImageMessage

/**
 * Create by ChenLei on 2020/12/7
 * Describe: 图片ViewHolder
 */
class ImageViewHolder(
    private val mContext: Context,
    private val mViewGroup: ViewGroup,
    private val mRecyclerView: RecyclerView
) : ImMessageViewHolder<ViewGroup, ImageMessage>(mContext, mViewGroup) {

    init {
        mRecyclerView.setOnTouchListener { v, event ->
            ChatAdapter.touched = true
            return@setOnTouchListener false
        }
    }

    lateinit var ivImage: ImageView
    lateinit var tvProgress: TextView

    override fun createMessageView(): ViewGroup {
        val result = LayoutInflater.from(mContext)
            .inflate(R.layout.item_im_image, mViewGroup, false) as ViewGroup
        ivImage = result.findViewById(R.id.ivImage)
        tvProgress = result.findViewById(R.id.tvProgress)
        return result
    }

    override fun onMessage(isSelf: Boolean, messageView: ViewGroup, message: ImageMessage) {
        if (message.uploadProgress > 0 && message.uploadProgress < 1) {
            tvProgress.visibility = View.VISIBLE
            ivImage.visibility = View.GONE
            tvProgress.text = "${(message.uploadProgress * 100).toInt()}%"
        } else {
            val url = if (message.fileEntity.localPath?.isNotBlank() == true)
                message.fileEntity.localPath
            else
                message.getFileHttpUrl()
            tvProgress.visibility = View.GONE
            ivImage.visibility = View.VISIBLE
            Glide.with(mContext).load(url).addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    (mRecyclerView.layoutManager as? LinearLayoutManager)?.let {
                        // 如果图片加载完成的时候，还显示第一行的话，就滚到第一行
                        if (!ChatAdapter.touched) {
                            it.scrollToPositionWithOffset(0, 0)
                        }
                    }
                    return false
                }
            }).into(ivImage)
        }
    }

}
