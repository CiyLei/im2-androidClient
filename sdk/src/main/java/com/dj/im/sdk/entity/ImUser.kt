package com.dj.im.sdk.entity

import android.os.Parcel
import android.os.Parcelable


/**
 * Create by ChenLei on 2020/4/18
 * Describe: 用户对象
 */
data class ImUser(val id: Long, val userName: String, val alias: String, val avatarUrl: String) :
    Parcelable {
    constructor(source: Parcel) : this(
        source.readLong(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(userName)
        writeString(alias)
        writeString(avatarUrl)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ImUser> = object : Parcelable.Creator<ImUser> {
            override fun createFromParcel(source: Parcel): ImUser = ImUser(source)
            override fun newArray(size: Int): Array<ImUser?> = arrayOfNulls(size)
        }
    }
}
