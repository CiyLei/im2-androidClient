package com.dj.im.sdk.task

import com.dj.im.sdk.entity.BaseResponse
import com.dj.im.sdk.entity.HttpImUser
import com.dj.im.sdk.entity.RBGetUserInfoByConversationKey
import com.dj.im.sdk.net.RetrofitManager
import io.reactivex.Observable

/**
 * Create by ChenLei on 2020/10/22
 * Describe: 根据会话key获取会话中的用户列表
 */
open class HttpGetUserInfoByConversationKey(private val mConversationKey: String) :
    HttpGetUserInfoByIds(emptyList()) {

    override fun httpTask(): Observable<BaseResponse<List<HttpImUser>>> {
        return RetrofitManager.instance.apiStore.getUserInfoByConversationKey(
            RBGetUserInfoByConversationKey(mConversationKey)
        )
    }
}