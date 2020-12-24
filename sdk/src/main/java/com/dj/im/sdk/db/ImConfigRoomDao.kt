package com.dj.im.sdk.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.dj.im.sdk.entity.ConfigEntity

/**
 * Create by ChenLei on 2020/12/24
 * Describe: 配置数据库Dao
 */
@Dao
internal interface ImConfigRoomDao : ImBaseDao<ConfigEntity> {

    @Query("select * from config where `key` = :key")
    fun getValue(key: String): ConfigEntity?
}