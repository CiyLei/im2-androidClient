package com.dj.im.sdk.net

import com.dj.im.sdk.Constant
import com.dj.im.sdk.entity.*
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


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

    /**
     * 上传文件
     */
    @POST(Constant.URL.UPLOAD)
    fun upload(@Body requestBody: RequestBody?): Observable<BaseResponse<String>>

    /**
     * 获取历史消息列表
     */
    @POST(Constant.URL.GET_HISTORY_MESSAGE_LIST)
    fun getHistoryMessageList(@Body requestBody: RBGetHistoryMessageList): Observable<BaseResponse<List<HttpImMessage>>>

    /**
     * 获取群组消息
     */
    @POST(Constant.URL.GET_GROUP_INFO_BY_IDS)
    fun getGroupInfoByIds(@Body requestBody: RBGetGroupInfo): Observable<BaseResponse<List<HttpImGroup>>>

    /**
     * 根据id列表获取用户信息
     */
    @POST(Constant.URL.GET_USER_INFO_BY_IDS)
    fun getUserInfoByIds(@Body requestBody: RBGetUserInfoByIds): Observable<BaseResponse<List<HttpImUser>>>

    /**
     * 根据id列表获取用户信息
     */
    @POST(Constant.URL.GET_USER_INFO_BY_NAMES)
    fun getUserInfoByNames(@Body requestBody: RBGetUserInfoByNames): Observable<BaseResponse<List<HttpImUser>>>

    /**
     * 根据会话key获取会话中的用户列表
     */
    @POST(Constant.URL.GET_USER_INFO_BY_CONVERSATION_KEY)
    fun getUserInfoByConversationKey(@Body requestBody: RBGetUserInfoByConversationKey): Observable<BaseResponse<List<HttpImUser>>>
}
