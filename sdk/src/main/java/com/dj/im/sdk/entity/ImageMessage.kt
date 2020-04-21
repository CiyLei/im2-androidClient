package com.dj.im.sdk.entity

import com.google.gson.Gson
import java.io.File

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 图标消息
 */
class ImageMessage : FileMessage {

    constructor(file: File) : super(
        ImMessage(
            data = Gson().toJson(FileEntity(file.absolutePath, file.name)),
            type = ImMessage.Type.IMAGE,
            summary = "[图片]"
        )
    ) {
        fileEntity = FileEntity(file.absolutePath, file.name)
    }

    constructor(imMessage: ImMessage) : super(imMessage)
}