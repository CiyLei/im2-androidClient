package com.dj.im.sdk.convert.send

import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.task.SendBigTextMessageTask
import com.dj.im.sdk.task.SendFileMessageTask
import com.dj.im.sdk.task.SendTextMessageTask

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 获取发送消息任务的工厂
 */
object SendMessageTaskFactory {

    val sendMessageTasks = arrayListOf<Class<out AbsSendMessageTask>>(
        SendBigTextMessageTask::class.java,
        SendFileMessageTask::class.java
    )

    fun matchMessageTask(message: Message): AbsSendMessageTask? {
        sendMessageTasks.forEach {
            // 寻找发送此消息的任务
            val sendMessage = it.newInstance().matchTask(message)
            if (sendMessage != null) {
                return sendMessage
            }
        }
        // 保底发送文字消息
        return SendTextMessageTask().matchTask(message)
    }
}