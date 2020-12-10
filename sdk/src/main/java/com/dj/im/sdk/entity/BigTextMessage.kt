package com.dj.im.sdk.entity

import com.dj.im.sdk.service.ServiceManager
import com.google.gson.Gson
import java.io.File

/**
 * Create by ChenLei on 2020/4/22
 * Describe: 大文本消息
 */
class BigTextMessage : FileMessage {

    /**
     * 手动指定发送大文本消息
     * @param prefix 提示前缀
     */
    constructor(prefix: String, file: File) : super(
        ImMessage(
            ServiceManager.instance.mAppId,
            ServiceManager.instance.getUserInfo()?.userName ?: "",
            data = Gson().toJson(FileEntity(file.absolutePath, file.name)),
            type = ImMessage.Type.BIG_TEXT,
            summary = "[文本:${prefix}...]"
        )
    )

    constructor(imMessage: ImMessage) : super(imMessage)
}