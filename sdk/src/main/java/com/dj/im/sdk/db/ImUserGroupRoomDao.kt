package com.dj.im.sdk.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.dj.im.sdk.entity.UserGroupEntity

/**
 * Create by ChenLei on 2020/12/9
 * Describe: IM未读库操作
 */
@Dao
internal interface ImUserGroupRoomDao : ImBaseDao<UserGroupEntity> {
    /**
     * 获取群的用户列表
     */
    @Query("select * from UserGroup where belongAppId = :belongAppId and belongUserName = :belongUserName and groupId = :groupId ")
    fun getUserGroupList(
        belongAppId: String,
        belongUserName: String, groupId: Long
    ): List<UserGroupEntity>
}