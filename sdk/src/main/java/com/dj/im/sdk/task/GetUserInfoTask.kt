package com.dj.im.sdk.task

import android.os.Handler
import android.os.Looper
import com.dj.im.sdk.Constant
import com.dj.im.sdk.ITask
import com.dj.im.sdk.proto.PrGetUserInfo
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.utils.MessageConvertUtil

/**
 * Create by ChenLei on 2020/4/26
 * Describe: 获取用户信息任务（弃用）
 */
class GetUserInfoTask(private val mUserId: Long) : ITask.Stub() {

    private val mHandler = Handler(Looper.getMainLooper())

    override fun onCmdId(): Int = Constant.CMD.GET_USER_INFO

    override fun onReq2Buf(): ByteArray = PrGetUserInfo.GetUserInfoRequest.newBuilder()
        .setUserId(mUserId).build().toByteArray()

    override fun onBuf2Resp(buf: ByteArray?) {
        val response = PrResponseMessage.Response.parseFrom(buf)
        if (response.success) {
            // 获取用户信息成功，保存消息
            val userId = ServiceManager.instance.getUserInfo()?.id
            if (userId != null) {
                val userInfoResponse = PrGetUserInfo.GetUserInfoResponse.parseFrom(response.data)
                ServiceManager.instance.getDb()
                    ?.addUser(userId, MessageConvertUtil.prUser2ImUser(userInfoResponse.userInfo))
                // 通知回调
                mHandler.post {
                    ServiceManager.instance.imListeners.forEach {
                        it.onUserInfoChange(userInfoResponse.userInfo.userId)
                    }
                }
            }
        }
    }

    override fun onTaskEnd(errType: Int, errCode: Int) {

    }
}