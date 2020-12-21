package com.dj.im.sdk.service.handler

import com.dj.im.sdk.entity.ImConversation
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.UnReadMessage
import com.dj.im.sdk.proto.PrPushMessage
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.service.ImService
import com.dj.im.sdk.task.HttpGetGroupInfoTask
import com.dj.im.sdk.task.HttpGetUserInfoByNames
import com.dj.im.sdk.utils.MessageConvertUtil


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 推送消息的处理器
 */
internal class PushMessageHandler(private val mService: ImService) : IPushHandler {

    override fun onHandle(response: PrResponseMessage.Response) {
        val userInfo = mService.userInfo ?: return
        // 有推送消息
        val pushResponse = PrPushMessage.PushMessageResponse.parseFrom(response.data)
        // 保存未读信息
        val unReadUserNameList = pushResponse.unReadUserNameListList
        val message = MessageConvertUtil.prPushMessage2ImMessage(
            mService.appKey,
            userInfo.userName,
            pushResponse
        )
        mService.dbDao.addUnReadMessage(
            mService.appKey,
            userInfo.userName,
            ArrayList(unReadUserNameList.map {
                UnReadMessage(mService.appKey, userInfo.userName, message.id, it)
            })
        )
        // 保存消息
        mService.dbDao.addPushMessage(mService.appKey, userInfo.userName, message)
        // 如果本地是否有消息发送方的用户信息，如果没有的话，就获取
        val fromUser =
            mService.dbDao.getUser(mService.appKey, userInfo.userName, message.fromUserName)
        if (fromUser == null) {
            mService.imServiceStub.compositeDisposable.add(HttpGetUserInfoByNames(listOf(message.fromUserName)).start())
        }
        if (message.conversationType == ImConversation.Type.GROUP) {
            // 如果是群聊的话，看看有没有群信息，没有的话就获取
            if (mService.dbDao.getGroupInfo(
                    mService.appKey,
                    userInfo.userName,
                    message.toUserName.toLong()
                ) == null
            ) {
                mService.imServiceStub.compositeDisposable.add(HttpGetGroupInfoTask(listOf(message.toUserName.toLong())).start())
            }
        }
        // 添加会话
        mService.dbDao.addConversationForPushMessage(mService.appKey, userInfo.userName, message)
        // 通知更新
        mService.marsListener?.onPushMessage(pushResponse.id)
        mService.marsListener?.onChangeConversions()
        mService.marsListener?.onChangeMessageState(
            pushResponse.conversationKey,
            pushResponse.id,
            ImMessage.State.SUCCESS
        )
    }

}
