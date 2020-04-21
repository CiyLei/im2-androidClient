package com.dj.im.adapter

import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.MultipleItemRvAdapter
import com.dj.im.sdk.convert.message.Message

class MessageAdapter(data: List<Message>) : MultipleItemRvAdapter<Message, BaseViewHolder>(data) {

    init {
        finishInitialize()
    }

    override fun registerItemProvider() {
        mProviderDelegate.registerProvider(TextMessageAdapter())
        mProviderDelegate.registerProvider(FileMessageAdapter())
    }

    override fun getViewType(t: Message?): Int = t?.imMessage?.type ?: 0
}