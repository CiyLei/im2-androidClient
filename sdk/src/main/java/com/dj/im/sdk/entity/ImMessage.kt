package com.dj.im.sdk.entity;

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.os.Parcel
import android.os.Parcelable
import com.dj.im.sdk.Constant
import com.dj.im.sdk.service.ServiceManager
import java.util.*
import kotlin.collections.ArrayList

/**
 * Create by ChenLei on 2020/4/17
 * Describe: Im消息
 */
@Entity(
    tableName = "Message",
    primaryKeys = ["belongAppId", "belongUserName", "id"],
    indices = [Index("conversationKey")]
)
data class ImMessage(
    /**
     * 在数据库中表示这条消息是属于哪个应用缓存的
     */
    @ColumnInfo(name = "belongAppId")
    var belongAppId: String,

    /**
     * 在数据库中表示这条消息是属于哪个用户缓存的
     */
    @ColumnInfo(name = "belongUserName")
    var belongUserName: String,

    /**
     * 消息id
     */
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    /**
     * 会话id（单聊:MD5(低位用户id + 高位用户id)，群聊:群Id）
     */
    @ColumnInfo(name = "conversationKey")
    var conversationKey: String = "",

    /**
     * 会话类别（0:单聊、1:群聊）
     */
    @ColumnInfo(name = "conversationType")
    var conversationType: Int = Constant.ConversationType.SINGLE,

    /**
     * 发送方用户id
     */
    @ColumnInfo(name = "fromUserName")
    var fromUserName: String = "",

    /**
     * 接收方用户id（群聊为群聊id）
     */
    @ColumnInfo(name = "toUserName")
    var toUserName: String = "",

    /**
     * 消息类别（0:文字，1:图片，2:视频，3:语音，1000+:定为自定义消息体）
     */
    @ColumnInfo(name = "type")
    var type: Int = Type.TEXT,

    /**
     * 消息内容（如果类型复杂，可以是json，但最好提取出摘要放入summary字段以便搜索）
     */
    @ColumnInfo(name = "data")
    var data: String = "",

    /**
     * 消息内容的摘要（作为为消息记录的搜索字段，如果这字段为空则以data字段进行搜索）
     */
    @ColumnInfo(name = "summary")
    var summary: String = "",

    /**
     * 创建时间
     */
    @ColumnInfo(name = "createTime")
    var createTime: Long = Date().time,

    /**
     * 发送状态
     * 0:发送成功、接收成功；1:发送中；2:发送失败
     */
    @ColumnInfo(name = "state")
    var state: Int = State.SUCCESS,

    /**
     * 临时的未读消息列表
     */
    @Ignore
    internal val unReadUserName: ArrayList<String> = ArrayList()
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
         * 语音类型
         */
        const val VOICE = 3

        /**
         * 文件类型
         */
        const val FILE = 4

        /**
         * 大文本类型
         */
        const val BIG_TEXT = 5
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

    constructor() : this("", "")

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readLong(),
        source.readString(),
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readLong(),
        source.readInt(),
        ArrayList<String>().apply { source.readList(this, String::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(belongAppId)
        writeString(belongUserName)
        writeLong(id)
        writeString(conversationKey)
        writeInt(conversationType)
        writeString(fromUserName)
        writeString(toUserName)
        writeInt(type)
        writeString(data)
        writeString(summary)
        writeLong(createTime)
        writeInt(state)
        writeList(unReadUserName)
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
        val appId = ServiceManager.instance.mAppKey
        val userName = ServiceManager.instance.getUserInfo()?.userName ?: return
        ServiceManager.instance.getUserInfo()?.id?.let {
            ServiceManager.instance.getDb()?.addPushMessage(appId, userName, this)
        }
    }

    fun clone(): ImMessage = ImMessage(
        belongAppId,
        belongUserName,
        id,
        conversationKey,
        conversationType,
        fromUserName,
        toUserName,
        type,
        data,
        summary,
        createTime,
        state,
        unReadUserName
    )
}
