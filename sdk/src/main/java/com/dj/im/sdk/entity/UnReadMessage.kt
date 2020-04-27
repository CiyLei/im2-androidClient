package com.dj.im.sdk.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable

/**
 * Create by ChenLei on 2020/4/26
 * Describe: 未读消息实体类
 */
@Entity(tableName = "UnReadMessage", primaryKeys = ["userId", "messageId", "unReadUserId"])
data class UnReadMessage(
    /**
     * 在数据库中表示这条消息是属于哪个用户缓存的
     */
    @ColumnInfo(name = "userId")
    var userId: Long,
    /**
     * 未读消息的id
     */
    @ColumnInfo(name = "messageId")
    var messageId: Long,
    /**
     * 未读用户的id
     */
    @ColumnInfo(name = "unReadUserId")
    var unReadUserId: Long
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readLong(),
        source.readLong(),
        source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(userId)
        writeLong(messageId)
        writeLong(unReadUserId)
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