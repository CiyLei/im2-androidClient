package com.dj.im.sdk.task

import com.dj.im.sdk.DJIM
import com.dj.im.sdk.entity.BaseResponse
import com.dj.im.sdk.entity.HttpImUser
import com.dj.im.sdk.entity.RBGetUserInfoByIds
import com.dj.im.sdk.net.RetrofitManager
import com.dj.im.sdk.service.ServiceManager
import io.reactivex.Observable

/**
 * Create by ChenLei on 2020/10/22
 * Describe: 根据id列表获取用户信息任务
 */
open class HttpGetUserInfoByIds(private val mUserIds: List<Long>) : HttpTask<List<HttpImUser>>() {

    override fun httpTask(): Observable<BaseResponse<List<HttpImUser>>> {
        return RetrofitManager.instance.apiStore.getUserInfoByIds(RBGetUserInfoByIds(mUserIds))
    }

    override fun handleSuccess(data: List<HttpImUser>) {
        super.handleSuccess(data)
        val loginUserName = ServiceManager.instance.getUserInfo()?.userName ?: return
        DJIM.getDefaultThreadPoolExecutor().submit {
            data.forEach { user ->
                ServiceManager.instance.getDb()?.addUser(
                    ServiceManager.instance.mAppKey,
                    loginUserName, user.toImUser(ServiceManager.instance.mAppKey, loginUserName)
                )
            }
            mHandler.post {
                data.forEach { user ->
                    // 通知更新
                    ServiceManager.instance.imListeners.forEach {
                        it.onUserInfoChange(user.id)
                    }
                }
            }
        }
    }
}