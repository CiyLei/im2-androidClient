package com.dj.im.sdk.entity

import java.io.Serializable


/**
 * Create by ChenLei on 2020/4/18
 * Describe: 用户对象
 */
data class User(val id: Long, val userName: String, val alias: String, val avatarUrl: String) :
    Serializable
