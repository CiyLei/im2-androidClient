package com.dj.im.sdk.task

import android.os.Handler
import android.os.Looper
import com.dj.im.sdk.Constant
import com.dj.im.sdk.ITask
import com.dj.im.sdk.proto.PrGetGroupInfo
import com.dj.im.sdk.proto.PrGetUserInfo
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.utils.MessageConvertUtil

/**
 * Create by ChenLei on 2020/4/27
 * Describe: 获取群信息任务
 */
class GetGroupInfoTask(private val mGroupId: Long) : ITask.Stub() {

    private val mHandler = Handler(Looper.getMainLooper())

    override fun onCmdId(): Int = Constant.CMD.GET_GROUP_INFO

    override fun onReq2Buf(): ByteArray = PrGetGroupInfo.GetGroupInfoRequest.newBuilder()
        .setGroupId(mGroupId).build().toByteArray()

    override fun onBuf2Resp(buf: ByteArray?) {
        val response = PrResponseMessage.Response.parseFrom(buf)
        if (response.success) {
            // 获取群信息成功，保存消息
            val userId = ServiceManager.instance.getUserInfo()?.id
            if (userId != null) {
                val groupInfoResponse = PrGetGroupInfo.GetGroupInfoResponse.parseFrom(response.data)
                ServiceManager.instance.getDb()
                    ?.addGroup(
                        userId,
                        MessageConvertUtil.prUser2ImGroup(groupInfoResponse.groupInfo)
                    )
                // 通知回调
                mHandler.post {
                    ServiceManager.instance.imListeners.forEach {
                        it.onGroupInfoChange(groupInfoResponse.groupInfo.groupId)
                    }
                }
            }
        }
    }

    override fun onTaskEnd(errType: Int, errCode: Int) {

    }
}