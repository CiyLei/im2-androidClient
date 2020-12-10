package com.dj.im.sdk.entity

import com.dj.im.sdk.service.ServiceManager
import com.google.gson.Gson
import java.io.File

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 图标消息
 */
open class ImageMessage : FileMessage {

    constructor(file: File) : super(
        ImMessage(
            ServiceManager.instance.mAppId,
            ServiceManager.instance.getUserInfo()?.userName ?: "",
            data = Gson().toJson(FileEntity(file.absolutePath, file.name)),
            type = ImMessage.Type.IMAGE,
            summary = "[图片]"
        )
    )

    constructor(imMessage: ImMessage) : super(imMessage)
}