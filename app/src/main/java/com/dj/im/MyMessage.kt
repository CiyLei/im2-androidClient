package com.dj.im

import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.commons.models.IUser
import java.util.*
import kotlin.collections.HashMap


/**
 * Create by ChenLei on 2020/4/14
 * Describe:
 */
class MyMessage(val user: IUser, text: String) : IMessage {
    private val id: Long
    private val text: String
    private var timeString: String? = null
    private val type: Int
    private var contentFile: String? = null
    private var duration: Long = 0
    override fun getMsgId(): String {
        return id.toString()
    }

    override fun getMessageStatus(): IMessage.MessageStatus = IMessage.MessageStatus.SEND_SUCCEED

    override fun getFromUser(): IUser {
        return user
    }

    fun setMediaFilePath(path: String?) {
        contentFile = path
    }

    fun setDuration(duration: Long) {
        this.duration = duration
    }

    override fun getDuration(): Long {
        return duration
    }

    override fun getExtras(): HashMap<String, String> = HashMap()

    fun setTimeString(timeString: String?) {
        this.timeString = timeString
    }

    override fun getTimeString(): String? {
        return timeString
    }

    override fun getType(): Int {
        return type
    }

    override fun getText(): String {
        return text
    }

    override fun getProgress(): String = "100"

    override fun getMediaFilePath(): String? {
        return contentFile
    }

    init {
        this.text = text
        this.type = IMessage.MessageType.SEND_TEXT.ordinal
        id = UUID.randomUUID().getLeastSignificantBits()
    }
}