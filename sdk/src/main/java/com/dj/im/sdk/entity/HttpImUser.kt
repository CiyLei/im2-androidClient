package com.dj.im.sdk.entity

import java.io.Serializable

/**
 * Create by ChenLei on 2020/10/21
 * Describe: http的用户对象
 */
data class HttpImUser(
    val id: Long,
    val appId: Long,
    val userName: String,
    val alias: String,
    val avatarUrl: String,
    val createTime: String,
    val updateTime: String,
    val extra: String
) : Serializable {

    fun toImUser(belongAppId: String, belongUserName: String): ImUser =
        ImUser(belongAppId, belongUserName, id, userName, alias, avatarUrl, extra)
}