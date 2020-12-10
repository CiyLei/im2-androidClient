package com.dj.im.sdk.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable

/**
 * Create by ChenLei on 2020/4/26
 * Describe: 未读消息实体类
 */
@Entity(
    tableName = "UnReadMessage",
    primaryKeys = ["belongAppId", "belongUserName", "messageId", "unReadUserName"]
)
data class UnReadMessage(
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
     * 未读消息的id
     */
    @ColumnInfo(name = "messageId")
    var messageId: Long,
    /**
     * 未读用户的id
     */
    @ColumnInfo(name = "unReadUserName")
    var unReadUserName: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readLong(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(belongAppId)
        writeString(belongUserName)
        writeLong(messageId)
        writeString(unReadUserName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UnReadMessage> =
            object : Parcelable.Creator<UnReadMessage> {
                override fun createFromParcel(source: Parcel): UnReadMessage = UnReadMessage(source)
                override fun newArray(size: Int): Array<UnReadMessage?> = arrayOfNulls(size)
            }
    }
}