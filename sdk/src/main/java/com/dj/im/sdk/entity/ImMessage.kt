package com.dj.im.sdk.entity;

import android.os.Parcel
import android.os.Parcelable
import com.dj.im.sdk.Constant
import com.dj.im.sdk.service.ServiceManager
import java.util.*

/**
 * Create by ChenLei on 2020/4/17
 * Describe: Im消息
 */
data class ImMessage(

    /**
     * 消息id
     */
    var id: Long = 0L,

    /**
     * 会话id（单聊:MD5(低位用户id + 高位用户id)，群聊:群Id）
     */
    var conversationId: String = "",

    /**
     * 会话类别（0:单聊、1:群聊）
     */
    var conversationType: Int = Constant.ConversationType.SINGLE,

    /**
     * 发送方用户id
     */
    var fromId: Long = 0L,

    /**
     * 接收方用户id（群聊为空）
     */
    var toId: Long = 0L,

    /**
     * 消息类别（0:文字，1:图片，2:视频，3:语音，1000+:定为自定义消息体）
     */
    var type: Int = Type.TEXT,

    /**
     * 消息内容（如果类型复杂，可以是json，但最好提取出摘要放入summary字段以便搜索）
     */
    var data: String = "",

    /**
     * 消息内容的摘要（作为为消息记录的搜索字段，如果这字段为空则以data字段进行搜索）
     */
    var summary: String = "",

    /**
     * 创建时间
     */
    var createTime: Date = Date(),

    /**
     * 发送状态
     * 0:发送成功、接收成功；1:发送中；2:发送失败
     */
    var state: Int = State.SUCCESS,

    /**
     * 是否已读
     */
    var isRead: Boolean = false
) : Parcelable {
    /**
     * 消息类型
     */
    object Type {
        /**
         * 文字类型
         */
        const val TEXT = 0
        /**
         * 图片类型
         */
        const val IMAGE = 1
        /**
         * 文件类型
         */
        const val FILE = 4
    }

    /**
     * 消息发送状态
     */
    object State {
        /**
         * 发送、接收成功
         */
        const val SUCCESS = 0

        /**
         * 发送中
         */
        const val LOADING = 1

        /**
         * 发送失败
         */
        const val FAIL = 2
    }

    /**
     * 消息描述
     */
    fun getSummaryDesc(): String = if (summary.isBlank())
        data
    else
        summary

    constructor(source: Parcel) : this(
        source.readLong(),
        source.readString(),
        source.readInt(),
        source.readLong(),
        source.readLong(),
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readSerializable() as Date,
        source.readInt(),
        1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(conversationId)
        writeInt(conversationType)
        writeLong(fromId)
        writeLong(toId)
        writeInt(type)
        writeString(data)
        writeString(summary)
        writeSerializable(createTime)
        writeInt(state)
        writeInt((if (isRead) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ImMessage> = object : Parcelable.Creator<ImMessage> {
            override fun createFromParcel(source: Parcel): ImMessage = ImMessage(source)
            override fun newArray(size: Int): Array<ImMessage?> = arrayOfNulls(size)
        }
    }

    /**
     * 保存到数据库中
     */
    internal fun save() {
        ServiceManager.instance.getUserInfo()?.id?.let {
            ServiceManager.instance.getDb()?.addPushMessage(it, this)
        }
    }
}
