package com.dj.im

import cn.jiguang.imui.commons.models.IUser




/**
 * Create by ChenLei on 2020/4/14
 * Describe:
 */
class DefaultUser(
    id: String,
    displayName: String,
    avatar: String
) :
    IUser {
    private val id: String
    private val displayName: String
    private val avatar: String
    override fun getId(): String {
        return id
    }

    override fun getDisplayName(): String {
        return displayName
    }

    override fun getAvatarFilePath(): String {
        return avatar
    }

    init {
        this.id = id
        this.displayName = displayName
        this.avatar = avatar
    }
}