package com.dj.im.sdk.task.conversation

import android.os.Handler
import android.os.Looper
import com.dj.im.sdk.Constant
import com.dj.im.sdk.ITask
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.message.ResponseMessage
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.task.message.Message
import com.dj.im.server.modules.im.message.PrGetHistoryMessage

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 获取历史消息
 */
internal class HistoryMessage(private val mConversationId: String, private val mMessageId: Long) :
    ITask.Stub() {

    private val mHandler = Handler(Looper.getMainLooper())

    override fun onCmdId(): Int = Constant.CMD.GET_HISTORY_MESSAGE

    override fun onReq2Buf(): ByteArray = PrGetHistoryMessage.GetHistoryMessageRequest.newBuilder()
        .setConversationId(mConversationId).setMessageId(mMessageId).build().toByteArray()

    override fun onBuf2Resp(buf: ByteArray?) {
        val response = ResponseMessage.Response.parseFrom(buf)
        if (response.success) {
            val userId = ServiceManager.instance.getUserId()
            if (userId != null) {
                // 回调历史消息
                val rsp = PrGetHistoryMessage.GetHistoryMessageResponse.parseFrom(response.data)
                rsp.messagesList.forEach {
                    // 添加消息
                    ServiceManager.instance.conversationDao.addPushMessage(userId, it)
                }
            }
        }
    }

    override fun onTaskEnd(errType: Int, errCode: Int) {
        // 从数据库中读取历史消息（请求成功已经保存到数据库，请求失败也返回数据库中的数据）
        ServiceManager.instance.getUserId()?.run {
            val historyMessage = ArrayList<Message>()
            if (errCode != 0) {
                // 网络获取失败，返回数据库中所有的历史消息
                historyMessage.addAll(
                    ServiceManager.instance.conversationDao.getHistoryMessage(
                        this,
                        mConversationId,
                        mMessageId
                    )
                )
            } else {
                // 网络获取成功，返回最新的20条历史消息
                historyMessage.addAll(
                    ServiceManager.instance.conversationDao.getHistoryMessage(
                        this,
                        mConversationId,
                        mMessageId,
                        Conversation.pageSize
                    )
                )
            }
            // 在主线程中回调
            mHandler.post {
                ServiceManager.instance.imListeners.forEach {
                    it.onReadHistoryMessage(
                        mConversationId,
                        historyMessage
                    )
                }
            }
        }
    }

}