package com.dj.im.sdk.task

import com.dj.im.sdk.entity.BaseResponse
import com.dj.im.sdk.net.RetrofitManager
import io.reactivex.Observable

/**
 * Create by ChenLei on 2020/12/15
 * Describe: 退出登录任务
 */
class HttpLogoutTask : HttpTask<Any>() {

    override fun httpTask(): Observable<BaseResponse<Any>> {
        return RetrofitManager.instance.apiStore.logout()
    }
}