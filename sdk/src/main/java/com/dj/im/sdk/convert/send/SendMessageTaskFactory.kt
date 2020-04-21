package com.dj.im.sdk.convert.send

import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.task.SendTextMessageTask

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 获取发送消息任务的工厂
 */
object SendMessageTaskFactory {

    val sendMessageTasks = ArrayList<AbsSendMessageTask>()

    fun sendMessageTask(message: Message) {
        sendMessageTasks.forEach {
            // 如果发送任务成功，结束
            if (it.sendMessage(message)) {
                return
            }
        }
        // 保底发送文字消息
        SendTextMessageTask().sendMessage(message)
    }
}