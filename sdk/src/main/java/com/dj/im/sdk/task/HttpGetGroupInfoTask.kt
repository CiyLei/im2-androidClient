package com.dj.im.sdk.task

import com.dj.im.sdk.DJIM
import com.dj.im.sdk.entity.BaseResponse
import com.dj.im.sdk.entity.HttpImGroup
import com.dj.im.sdk.entity.RBGetGroupInfo
import com.dj.im.sdk.net.RetrofitManager
import com.dj.im.sdk.service.ServiceManager
import io.reactivex.Observable

/**
 * Create by ChenLei on 2020/10/22
 * Describe: 获取群信息任务
 */
open class HttpGetGroupInfoTask(private val mGroupIds: List<Long>) : HttpTask<List<HttpImGroup>>() {

    override fun httpTask(): Observable<BaseResponse<List<HttpImGroup>>> {
        return RetrofitManager.instance.apiStore.getGroupInfoByIds(RBGetGroupInfo(mGroupIds))
    }

    override fun handleSuccess(data: List<HttpImGroup>) {
        super.handleSuccess(data)
        val loginUserName = ServiceManager.instance.getUserInfo()?.userName ?: return
        DJIM.getDefaultThreadPoolExecutor().submit {
            data.forEach { group ->
                // 把群信息添加到本地数据库
                ServiceManager.instance.getDb()?.addGroup(
                    ServiceManager.instance.mAppId,
                    loginUserName,
                    group.toImGroup(ServiceManager.instance.mAppId, loginUserName)
                )
            }
            mHandler.post {
                data.forEach { group ->
                    // 通知更新
                    ServiceManager.instance.imListeners.forEach {
                        it.onGroupInfoChange(group.id)
                    }
                }
            }
        }
    }

}