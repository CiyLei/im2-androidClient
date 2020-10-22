package com.dj.im.sdk.task

import com.dj.im.sdk.entity.BaseResponse
import com.dj.im.sdk.entity.HttpImMessage
import com.dj.im.sdk.entity.RBGetHistoryMessageList
import com.dj.im.sdk.net.RetrofitManager
import io.reactivex.Observable

/**
 * Create by ChenLei on 2020/10/22
 * Describe: 获取历史消息的任务
 */
internal class HttpGetHistoryMessageListTask(
    private val mConversationKey: String,
    private val mMessageId: Long
) : HttpTask<List<HttpImMessage>>() {

    override fun httpTask(): Observable<BaseResponse<List<HttpImMessage>>> {
        return RetrofitManager.instance.apiStore.getHistoryMessageList(
            RBGetHistoryMessageList(mConversationKey, mMessageId)
        )
    }

}