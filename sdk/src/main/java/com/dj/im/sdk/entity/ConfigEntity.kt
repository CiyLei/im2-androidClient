package com.dj.im.sdk.entity;

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity

/**
 * Create by ChenLei on 2020/12/24
 * Describe: 配置实体
 */
@Entity(tableName = "Config", primaryKeys = ["key"])
data class ConfigEntity(

    @ColumnInfo(name = "key")
    val key: String,

    @ColumnInfo(name = "value")
    var value: String
)
