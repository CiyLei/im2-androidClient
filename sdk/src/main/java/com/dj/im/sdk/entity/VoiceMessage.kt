package com.dj.im.sdk.entity

import com.dj.im.sdk.service.ServiceManager
import com.google.gson.Gson
import java.io.File
import kotlin.properties.Delegates

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 语音消息
 */
open class VoiceMessage : FileMessage {

    var duration by Delegates.notNull<Float>()

    constructor(file: File, duration: Float) : super(
        ImMessage(
            ServiceManager.instance.mAppKey,
            ServiceManager.instance.getUserInfo()?.userName ?: "",
            data = Gson().toJson(
                FileEntity(
                    file.absolutePath,
                    file.name,
                    extra = generateExtraMap(duration)
                )
            ),
            type = ImMessage.Type.VOICE,
            summary = "[语音]"
        )
    ) {
        this.duration = duration
    }

    constructor(imMessage: ImMessage) : super(imMessage) {
        // 从消息中获取秒数
        duration = fileEntity.extra[KEY_DURATION]?.toFloat() ?: 0f
    }

    companion object {

        const val KEY_DURATION = "duration"

        /**
         * 生成额外的属性
         * 这里是语音的秒数
         */
        fun generateExtraMap(duration: Float) = hashMapOf(KEY_DURATION to "$duration")
    }

}