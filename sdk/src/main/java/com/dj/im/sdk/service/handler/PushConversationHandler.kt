package com.dj.im.sdk.service.handler

import android.util.Log
import com.dj.im.sdk.message.ResponseMessage
import com.dj.im.sdk.service.ImService
import com.dj.im.server.modules.im.message.PushConversation


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 推送会话处理器
 */
internal class PushConversationHandler(private val mService: ImService) : IPushHandler {

    override fun onHandle(response: ResponseMessage.Response) {
        // 会话推送
        val conversationResponse =
            PushConversation.PushConversationResponse.parseFrom(response.data)
        // 先清空会话信息
        mService.conversationDao.clearConversation(mService.userInfo!!.id)
        // 保存到数据库中
        for (conversation in conversationResponse.conversationsList) {
            mService.conversationDao.addUser(
                mService.userInfo!!.id,
                conversation.toUserInfo
            )
            mService.conversationDao.addConversation(mService.userInfo!!.id, conversation)
        }
        // 通知回调
        mService.marsListener?.onChangeConversions()
        Log.d("MarsCallBack", conversationResponse.toString())
    }

}
