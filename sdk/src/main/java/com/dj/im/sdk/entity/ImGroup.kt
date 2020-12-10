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
@Entity(tableName = "Group", primaryKeys = ["belongAppId", "belongUserName", "id"])
data class ImGroup(
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
    var userNameList: List<String>
) : Parcelable {

    constructor() : this("", "", 0L, "", "", emptyList())

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readLong(),
        source.readString(),
        source.readString(),
        ArrayList<String>().apply { source.readList(this, String::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(belongAppId)
        writeString(belongUserName)
        writeLong(id)
        writeString(name)
        writeString(avatarUrl)
        writeList(userNameList)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ImGroup> = object : Parcelable.Creator<ImGroup> {
            override fun createFromParcel(source: Parcel): ImGroup = ImGroup(source)
            override fun newArray(size: Int): Array<ImGroup?> = arrayOfNulls(size)
        }
    }
}