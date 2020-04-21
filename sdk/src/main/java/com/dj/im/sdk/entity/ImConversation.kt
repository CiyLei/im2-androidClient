package com.dj.im.sdk.entity

import android.os.Parcel
import android.os.Parcelable
import com.dj.im.sdk.Constant

/**
 * Create by ChenLei on 2020/4/20
 * Describe: 会话
 */
data class ImConversation(
    /**
     * 会话id
     */
    val id: String,
    /**
     * 会话类型
     */
    val type: Int,
    /**
     * 未读数量
     */
    val unReadCount: Int,
    /**
     * 对方用户id
     */
    val otherSideUserId: Long
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readInt(),
        source.readInt(),
        source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeInt(type)
        writeInt(unReadCount)
        writeLong(otherSideUserId)
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