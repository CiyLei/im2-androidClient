package com.dj.im.sdk.convert.send

import android.os.Handler
import android.os.Looper
import com.dj.im.sdk.ITask
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.service.ServiceManager

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 发送消息任务的抽象
 */
abstract class AbsSendMessageTask() : ITask.Stub() {

    val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 发送消息
     *
     * @param message 需要发送的消息
     * @return 真正发送的消息，null为不是我要发送的
     */
    abstract fun sendMessage(message: Message): Message?

    /**
     * 开发发送
     */
    protected fun startSend() {
        ServiceManager.instance.sendTask(this)
    }

    /**
     * 添加到数据库中
     */
    protected fun save(message: ImMessage) {
        ServiceManager.instance.getUserInfo()?.id?.let {
            ServiceManager.instance.getDb()?.addPushMessage(it, message)
        }
    }

    /**
     * 通知状态变化
     */
    protected fun notifyChangeState(messageId: Long, state: Int) {
        // 在主线程中触发更改状态的回调
        mainHandler.post {
            ServiceManager.instance.imListeners.forEach {
                it.onChangeMessageSendState(messageId, state)
            }
        }
    }
}