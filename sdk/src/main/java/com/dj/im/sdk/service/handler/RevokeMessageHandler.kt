package com.dj.im.sdk.service.handler

import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.proto.PrRevokeMessage
import com.dj.im.sdk.service.ImService

/**
 * Create by ChenLei on 2021/1/11
 * Describe: 撤回消息推送的处理器
 */
internal class RevokeMessageHandler(private val mService: ImService) : IPushHandler {

    override fun onHandle(response: PrResponseMessage.Response) {
        val userInfo = mService.userInfo ?: return
        // 有推送撤回消息
        val prResponse = PrRevokeMessage.RevokeMessageResponse.parseFrom(response.data)
        // 读取撤回的消息是否存在本地
        val message =
            mService.dbDao.getMessageForId(mService.appKey, userInfo.userName, prResponse.messageId)
        if (message != null) {
            // 修改消息的撤回状态
            message.revoke = true
            mService.dbDao.addPushMessage(mService.appKey, userInfo.userName, message)
            // 通知回调
            mService.marsListener?.onRevokeMessage(prResponse.conversationKey, message.id)
            mService.marsListener?.onChangeConversions()
        }
    }
}