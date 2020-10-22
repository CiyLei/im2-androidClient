package com.dj.im.sdk.entity

import java.io.Serializable

/**
 * Create by ChenLei on 2020/10/21
 * Describe: 获取用户信息的请求数据
 */
data class RBGetUserInfoByConversationKey(val conversationKey: String) : Serializable