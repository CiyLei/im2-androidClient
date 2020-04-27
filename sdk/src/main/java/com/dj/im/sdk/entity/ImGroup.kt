package com.dj.im.sdk.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.os.Parcel
import android.os.Parcelable

/**
 * Create by ChenLei on 2020/4/27
 * Describe: 群信息
 */
@Entity(tableName = "Group", primaryKeys = ["userId", "id"])
data class ImGroup(
    /**
     * 群id
     */
    @ColumnInfo(name = "id")
    var id: Long,
    /**
     * 群名
     */
    @ColumnInfo(name = "name")
    var name: String,
    /**
     * 群头像地址
     */
    @ColumnInfo(name = "avatarUrl")
    var avatarUrl: String,
    /**
     * 群用户列表
     */
    @Ignore
    var userIdList: List<Long>,

    /**
     * 在数据库中表示这条消息是属于哪个用户缓存的
     */
    @ColumnInfo(name = "userId")
    var userId: Long = 0L
) : Parcelable {
    constructor() : this(0L, "", "", ArrayList<Long>())

    constructor(source: Parcel) : this(
        source.readLong(),
        source.readString(),
        source.readString(),
        ArrayList<Long>().apply { source.readList(this, Long::class.java.classLoader) },
        source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(name)
        writeString(avatarUrl)
        writeList(userIdList)
        writeLong(userId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ImGroup> = object : Parcelable.Creator<ImGroup> {
            override fun createFromParcel(source: Parcel): ImGroup = ImGroup(source)
            override fun newArray(size: Int): Array<ImGroup?> = arrayOfNulls(size)
        }
    }
}