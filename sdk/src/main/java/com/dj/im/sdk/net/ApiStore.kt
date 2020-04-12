package com.dj.im.sdk.net

import com.dj.im.sdk.Constant
import com.dj.im.sdk.entity.BaseResponse
import com.dj.im.sdk.entity.ServerSituationEntity
import io.reactivex.Observable
import retrofit2.http.GET


/**
 * Create by ChenLei on 2020/4/11
 * Describe: 所有网络请求的api
 */
internal interface ApiStore {

    /**
     * 获取所有可连接的Im服务器情况
     */
    @GET(Constant.URL.DNS)
    fun dns(): Observable<BaseResponse<ServerSituationEntity>>
}
