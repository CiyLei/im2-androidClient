package com.dj.im.sdk.service.handler

import com.dj.im.sdk.proto.PrPushMessage
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.service.ImService
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.utils.MessageConvertUtil


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 推送消息的处理器
 */
internal class PushMessageHandler(private val mService: ImService) : IPushHandler {

    override fun onHandle(response: PrResponseMessage.Response) {
        // 有推送消息
        val pushResponse = PrPushMessage.PushMessageResponse.parseFrom(response.data)
        // 保存消息
        mService.dbDao.addPushMessage(
            mService.userInfo!!.id,
            MessageConvertUtil.prPushMessage2ImMessage(pushResponse)
        )
        // 保存消息对方的用户消息
        mService.dbDao.addUser(
            mService.userInfo!!.id,
            MessageConvertUtil.prUser2ImUser(pushResponse.otherSideUserInfo)
        )
        // 添加会话
        mService.dbDao.addConversationForPushMessage(
            mService.userInfo!!.id,
            MessageConvertUtil.prPushMessage2ImMessage(pushResponse)
        )
        // 通知更新
        mService.marsListener?.onPushMessage(pushResponse.id)
        mService.marsListener?.onChangeConversions()
        mService.marsListener?.onChangeMessageState(
            pushResponse.id,
            ImMessage.State.SUCCESS
        )
    }

}
