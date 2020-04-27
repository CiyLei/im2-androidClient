package com.dj.im.sdk.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity

/**
 * Create by ChenLei on 2020/4/27
 * Describe: 用户群组关系表
 */
@Entity(tableName = "UserGroup", primaryKeys = ["userId", "groupId", "uId"])
data class UserGroupEntity(
    /**
     * 在数据库中表示这条消息是属于哪个用户缓存的
     */
    @ColumnInfo(name = "userId")
    var userId: Long = 0L,
    /**
     * 群id
     */
    @ColumnInfo(name = "groupId")
    var groupId: Long,
    /**
     * 用户id
     */
    @ColumnInfo(name = "uId")
    var uId: Long
)