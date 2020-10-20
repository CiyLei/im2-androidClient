package com.dj.im.sdk.task

import com.dj.im.sdk.Constant
import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.entity.BigTextMessage
import com.dj.im.sdk.entity.FileEntity
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.TextMessage
import com.dj.im.sdk.service.ServiceManager
import com.google.gson.Gson
import java.io.File
import java.util.*

/**
 * Create by ChenLei on 2020/4/22
 * Describe: 发送大文本消息任务
 */
open class SendBigTextMessageTask : SendFileMessageTask() {

    override fun sendMessage(message: Message): Message? {
        // 如果消息的内容长度大于指定的长度，改为发送大文本消息
        if (message is TextMessage && message.imMessage.data.length > Constant.MESSAGE_DATA_MAX_LENGTH) {
            // 摘要内存
            val summaryContent = "${message.imMessage.data.substring(0, 10)}。。。"
            // 将data保存到本地
            val tmpTxt =
                File("${ServiceManager.instance.application.cacheDir}/${summaryContent}.txt")
            if (!tmpTxt.exists()) {
                tmpTxt.createNewFile()
            }
            tmpTxt.writeText(message.imMessage.data)
            // 构造大文本消息，并保留数据
            val result = BigTextMessage(message.imMessage.clone().apply {
                data = Gson().toJson(FileEntity(tmpTxt.absolutePath, tmpTxt.name))
                type = ImMessage.Type.BIG_TEXT
                summary = "[文本:${summaryContent}]"
            })
            // 发送
            return super.sendMessage(result)
        }
        return super.sendMessage(message)
    }
}