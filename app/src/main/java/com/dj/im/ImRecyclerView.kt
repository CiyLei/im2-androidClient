package com.dj.im

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import cn.jiguang.imui.chatinput.emoji.EmoticonsKeyboardUtils.dip2px

/**
 * Create by ChenLei on 2021/1/12
 * Describe:
 */
class ImRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    private var mImLayoutManager: LinearLayoutManager = object : LinearLayoutManager(context) {
        /**
         * 设置预留空间比图片消息的最大高再高一点
         * 这样加载图片的时候就一定会提前加载加载，不会突兀，增加用户体验
         */
        override fun getExtraLayoutSpace(state: State?): Int {
            return dip2px(context, 500 * 1.2f)
        }
    }.apply {
        reverseLayout = true
        stackFromEnd = true

        // 滚到底部
        scrollToPositionWithOffset(0, 0)
    }
    private val mRect = Rect()

    // 消息列表上次的大小，监听键盘，保持底部不变
    private var mPreHeight = 0

    init {
        layoutManager = mImLayoutManager
        // 监听键盘出现，保存消息列表在底部
        viewTreeObserver.addOnGlobalLayoutListener {
            getWindowVisibleDisplayFrame(mRect)
            mPreHeight = if (mPreHeight == 0) {
                mRect.height()
            } else {
                val diff = mRect.height() - mPreHeight
                if (diff < 0) {
                    scrollBy(0, -diff)
                }
                mRect.height()
            }
        }
    }

    fun getImLayoutManager(): LinearLayoutManager = mImLayoutManager
}