package com.dj.im.sdk.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 会话
 */
@Entity(tableName = "Conversation", primaryKeys = ["belongAppId", "belongUserName", "key"])
data class ImConversation(
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
     * 会话id
     */
    @ColumnInfo(name = "key")
    var key: String,
    /**
     * 会话类型
     */
    @ColumnInfo(name = "type")
    var type: Int,
    /**
     * 未读数量
     */
    @ColumnInfo(name = "unReadCount")
    var unReadCount: Int,
    /**
     * 关联id，单聊的话为对方用户名称，群聊的话，为群id
     */
    @ColumnInfo(name = "associatedId")
    var associatedId: String
) : Parcelable {
    /**
     * 会话类型
     */
    object Type {
        /**
         * 单聊
         */
        const val SINGLE = 0

        /**
         * 群聊
         */
        const val GROUP = 1
    }

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readInt(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(belongAppId)
        writeString(belongUserName)
        writeString(key)
        writeInt(type)
        writeInt(unReadCount)
        writeString(associatedId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ImConversation> =
            object : Parcelable.Creator<ImConversation> {
                override fun createFromParcel(source: Parcel): ImConversation =
                    ImConversation(source)

                override fun newArray(size: Int): Array<ImConversation?> = arrayOfNulls(size)
            }
    }
}