package com.dj.im.sdk.service.handler

import android.util.Log
import com.dj.im.sdk.entity.ImConversation
import com.dj.im.sdk.proto.PrPushConversation
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.service.ImService
import com.dj.im.sdk.utils.MessageConvertUtil


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 推送会话处理器
 */
internal class PushConversationHandler(private val mService: ImService) : IPushHandler {

    override fun onHandle(response: PrResponseMessage.Response) {
        val userInfo = mService.userInfo ?: return
        // 会话推送
        val conversationResponse =
            PrPushConversation.PushConversationResponse.parseFrom(response.data)
        // 先清空会话信息
        mService.dbDao.clearConversation(mService.appKey, userInfo.userName)
        // 保存到数据库中
        for (conversation in conversationResponse.conversationsList) {
            if (conversation.conversationType == ImConversation.Type.SINGLE) {
                // 如果是单聊的话，先保存用户信息
                mService.dbDao.addUser(
                    mService.appKey,
                    userInfo.userName,
                    MessageConvertUtil.prUser2ImUser(
                        mService.appKey,
                        userInfo.userName,
                        conversation.otherSideUserInfo
                    )
                )
            } else if (conversation.conversationType == ImConversation.Type.GROUP) {
                // 如果是群聊的话，先保存群聊信息
                mService.dbDao.addGroup(
                    mService.appKey,
                    userInfo.userName,
                    MessageConvertUtil.prUser2ImGroup(
                        mService.appKey,
                        userInfo.userName,
                        conversation.groupInfo
                    )
                )
            }
            // 添加会话
            mService.dbDao.addConversation(mService.appKey, userInfo.userName, conversation)
        }
        // 通知回调
        mService.marsListener?.onChangeConversions()
        Log.d("MarsCallBack", conversationResponse.toString())
    }

}
