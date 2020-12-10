package com.dj.im.sdk.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.dj.im.sdk.entity.ImUser

/**
 * Create by ChenLei on 2020/12/9
 * Describe: IM用户库操作
 */
@Dao
internal interface ImUserRoomDao : ImBaseDao<ImUser> {

    /**
     * 获取用户信息
     */
    @Query("select * from user where belongAppId = :belongAppId and belongUserName = :belongUserName and userName = :userName")
    fun getUser(belongAppId: String, belongUserName: String, userName: String): ImUser?
}