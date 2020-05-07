package com.dj.im.sdk.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable


/**
 * Create by ChenLei on 2020/4/18
 * Describe: 用户对象
 */
@Entity(tableName = "User", primaryKeys = ["userId", "id"])
data class ImUser(
    /**
     * 用户id
     */
    @ColumnInfo(name = "id")
    var id: Long,
    /**
     * 用户名
     */
    @ColumnInfo(name = "userName")
    var userName: String,
    /**
     * 用户别名(昵称)
     */
    @ColumnInfo(name = "alias")
    var alias: String,
    /**
     * 用户头像地址
     */
    @ColumnInfo(name = "avatarUrl")
    var avatarUrl: String,
    /**
     * 用户额外信息
     */
    @ColumnInfo(name = "extra")
    var extra: String = "",

    /**
     * 在数据库中表示这条消息是属于哪个用户缓存的
     */
    @ColumnInfo(name = "userId")
    var userId: Long = 0L
) :
    Parcelable {
    constructor(source: Parcel) : this(
        source.readLong(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(userName)
        writeString(alias)
        writeString(avatarUrl)
        writeString(extra)
        writeLong(userId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ImUser> = object : Parcelable.Creator<ImUser> {
            override fun createFromParcel(source: Parcel): ImUser = ImUser(source)
            override fun newArray(size: Int): Array<ImUser?> = arrayOfNulls(size)
        }
    }
}
