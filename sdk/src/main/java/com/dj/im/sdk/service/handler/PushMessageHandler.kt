package com.dj.im.sdk.service.handler

import com.dj.im.sdk.proto.PrPushMessage
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.service.ImService
import com.dj.im.sdk.task.message.Message


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 推送消息的处理器
 */
internal class PushMessageHandler(private val mService: ImService) : IPushHandler {

    override fun onHandle(response: PrResponseMessage.Response) {
        // 有推送消息
        val pushResponse = PrPushMessage.PushMessageResponse.parseFrom(response.data)
        // 保存消息
        mService.conversationDao.addPushMessage(mService.userInfo!!.id, pushResponse)
        // 保存消息对方的用户消息
        mService.conversationDao.addUser(
            mService.userInfo!!.id,
            pushResponse.otherSideUserInfo
        )
        // 添加会话
        mService.conversationDao.addConversationForPushMessage(
            mService.userInfo!!.id,
            pushResponse
        )
        mService.marsListener?.onPushMessage(pushResponse.id)
        mService.marsListener?.onChangeConversions()
        mService.marsListener?.onChangeMessageState(
            pushResponse.id,
            Message.State.SUCCESS
        )
    }

}
