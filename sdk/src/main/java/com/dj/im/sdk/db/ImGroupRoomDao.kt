package com.dj.im.sdk.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.dj.im.sdk.entity.ImGroup

/**
 * Create by ChenLei on 2020/12/9
 * Describe: IM群组库操作
 */
@Dao
internal interface ImGroupRoomDao : ImBaseDao<ImGroup> {

    /**
     * 查询群消息
     */
    @Query("select * from `Group` where belongAppId = :belongAppId and belongUserName = :belongUserName and id = :groupId")
    fun getGroupInfo(belongAppId: String, belongUserName: String, groupId: Long): ImGroup?
}