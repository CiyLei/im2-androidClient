package com.dj.im.sdk.service.handler

import com.dj.im.sdk.entity.ImConversation
import com.dj.im.sdk.proto.PrPushMessage
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.service.ImService
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.UnReadMessage
import com.dj.im.sdk.task.GetGroupInfoTask
import com.dj.im.sdk.task.GetUserInfoTask
import com.dj.im.sdk.utils.MessageConvertUtil


/**
 * Create by ChenLei on 2020/4/19
 * Describe: 推送消息的处理器
 */
internal class PushMessageHandler(private val mService: ImService) : IPushHandler {

    override fun onHandle(response: PrResponseMessage.Response) {
        // 有推送消息
        val pushResponse = PrPushMessage.PushMessageResponse.parseFrom(response.data)
        // 保存未读信息
        val unReadUserIdList = pushResponse.unReadUserIdListList
        val message = MessageConvertUtil.prPushMessage2ImMessage(pushResponse)
        mService.dbDao.addUnReadMessage(mService.userInfo!!.id, ArrayList(unReadUserIdList.map {
            UnReadMessage(mService.userInfo!!.id, message.id, it)
        }))
        // 保存消息
        mService.dbDao.addPushMessage(
            mService.userInfo!!.id,
            message
        )
        // 如果本地是否有消息发送方的用户信息，如果没有的话，就获取
        val fromUser = mService.dbDao.getUser(mService.userInfo!!.id, message.fromId)
        if (fromUser == null) {
            mService.imServiceStub.sendTask(GetUserInfoTask(message.fromId))
        }
        if (message.conversationType == ImConversation.Type.GROUP) {
            // 如果是群聊的话，看看有没有群信息，没有的话就获取
            if (mService.dbDao.getGroupInfo(mService.userInfo!!.id, message.toId) == null) {
                mService.imServiceStub.sendTask(GetGroupInfoTask(message.toId))
            }
        }
        // 添加会话
        mService.dbDao.addConversationForPushMessage(
            mService.userInfo!!.id,
            message
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
