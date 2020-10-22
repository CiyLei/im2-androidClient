package com.dj.im.sdk.entity

import java.io.Serializable

/**
 * Create by ChenLei on 2020/10/21
 * Describe: http的用户对象
 */
data class HttpImGroup(
    val id: Long,
    val name: String,
    val avatarUrl: String,
    val userIdList: List<Long>
) : Serializable {

    fun toImGroup(userId: Long): ImGroup = ImGroup(id, name, avatarUrl, userIdList, userId)
}