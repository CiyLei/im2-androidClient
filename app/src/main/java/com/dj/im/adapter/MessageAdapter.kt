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
    }

    override fun getViewType(t: Message?): Int = t?.getImMessage()?.type ?: 0
}