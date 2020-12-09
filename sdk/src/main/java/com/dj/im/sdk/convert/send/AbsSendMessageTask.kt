package com.dj.im.sdk.convert.send

import android.os.Handler
import android.os.Looper
import com.dj.im.sdk.ITask
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.service.ServiceManager
import io.reactivex.disposables.CompositeDisposable

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 发送消息任务的抽象
 */
abstract class AbsSendMessageTask() : ITask.Stub() {

    val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 匹配发送的任务
     */
    abstract fun matchTask(message: Message): AbsSendMessageTask?

    abstract fun getMessage(): Message

    /**
     * 开发发送
     */
    open fun startSend() {
        ServiceManager.instance.sendTask(this)
    }

    /**
     * 通知状态变化
     */
    protected fun notifyChangeState(conversationKey: String, messageId: Long, state: Int) {
        // 在主线程中触发更改状态的回调
        mainHandler.post {
            ServiceManager.instance.imListeners.forEach {
                it.onChangeMessageSendState(conversationKey, messageId, state)
            }
        }
    }
}