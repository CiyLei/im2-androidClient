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
    val userNameList: List<String>
) : Serializable {

    fun toImGroup(belongAppId: String, belongUserName: String): ImGroup =
        ImGroup(belongAppId, belongUserName, id, name, avatarUrl, userNameList)
}