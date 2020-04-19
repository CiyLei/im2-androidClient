package com.dj.im.sdk.service.handler

import com.dj.im.sdk.message.PrPushReadConversation
import com.dj.im.sdk.message.ResponseMessage
import com.dj.im.sdk.service.ImService


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 推送会话已读处理器
 */
internal class PushReadConversationHandler(private val mService: ImService) : IPushHandler {

    override fun onHandle(response: ResponseMessage.Response) {
        // 已读推送
        val readResponse =
            PrPushReadConversation.PushReadConversationResponse.parseFrom(
                response.data
            )
        // 已读方是不是自己
        val isSelf = readResponse.readUserId == mService.userInfo?.id
        if (isSelf) {
            // 如果是自己已读，则清空会话的未读数量
            mService.conversationDao.clearConversationUnReadCount(
                mService.userInfo!!.id,
                readResponse.conversationId
            )
            mService.marsListener?.onChangeConversions()
        } else {
            // 如果是会话对方已读，则设置会话中的消息全部已读
            mService.conversationDao.readConversationMessage(
                mService.userInfo!!.id,
                readResponse.conversationId
            )
            mService.marsListener?.onChangeConversationRead(readResponse.conversationId)
            mService.marsListener?.onChangeConversions()
        }
    }

}
