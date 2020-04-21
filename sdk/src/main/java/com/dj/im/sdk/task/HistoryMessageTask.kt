package com.dj.im.sdk.task

import android.os.Handler
import android.os.Looper
import com.dj.im.sdk.Constant
import com.dj.im.sdk.ITask
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.convert.message.MessageConvertFactory
import com.dj.im.sdk.proto.PrGetHistoryMessage
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.utils.MessageConvertUtil

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 获取历史消息
 */
internal class HistoryMessageTask(
    private val mConversationId: String,
    private val mMessageId: Long
) :
    ITask.Stub() {

    private val mHandler = Handler(Looper.getMainLooper())

    override fun onCmdId(): Int = Constant.CMD.GET_HISTORY_MESSAGE

    override fun onReq2Buf(): ByteArray = PrGetHistoryMessage.GetHistoryMessageRequest.newBuilder()
        .setConversationId(mConversationId).setMessageId(mMessageId).build().toByteArray()

    override fun onBuf2Resp(buf: ByteArray?) {
        val response = PrResponseMessage.Response.parseFrom(buf)
        if (response.success) {
            val userId = ServiceManager.instance.getUserInfo()?.id
            if (userId != null) {
                // 回调历史消息
                val rsp = PrGetHistoryMessage.GetHistoryMessageResponse.parseFrom(response.data)
                rsp.messagesList.forEach {
                    // 添加消息
                    ServiceManager.instance.getDb()
                        ?.addPushMessage(userId, MessageConvertUtil.prPushMessage2ImMessage(it))
                }
            }
        }
    }

    override fun onTaskEnd(errType: Int, errCode: Int) {
        // 从数据库中读取历史消息（请求成功已经保存到数据库，请求失败也返回数据库中的数据）
        ServiceManager.instance.getUserInfo()?.id?.run {
            val historyMessage = ArrayList<ImMessage>()
            if (errCode != 0) {
                // 网络获取失败，返回数据库中100条的历史消息
                ServiceManager.instance.getDb()?.getHistoryMessage(
                    this,
                    mConversationId,
                    mMessageId,
                    100
                )?.forEach { m ->
                    historyMessage.add(m)
                }
            } else {
                // 网络获取成功，返回最新的20条历史消息
                ServiceManager.instance.getDb()?.getHistoryMessage(
                    this,
                    mConversationId,
                    mMessageId,
                    Conversation.pageSize
                )?.forEach { m ->
                    historyMessage.add(m)
                }
            }
            // 在主线程中回调
            mHandler.post {
                ServiceManager.instance.imListeners.forEach {
                    it.onReadHistoryMessage(
                        mConversationId,
                        historyMessage.map { m -> MessageConvertFactory.convert(m) }
                    )
                }
            }
        }
    }

}