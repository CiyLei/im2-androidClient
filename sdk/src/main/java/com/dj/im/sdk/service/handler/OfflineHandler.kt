package com.dj.im.sdk.service.handler

import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.service.ImService

/**
 * Create by ChenLei on 2020/4/22
 * Describe: 下线处理器
 */
internal class OfflineHandler(private val mService: ImService) : IPushHandler {

    override fun onHandle(response: PrResponseMessage.Response) {
        mService.marsListener?.onOffline(response.code, response.msg)
        mService.closeMars()
        mService.clearToken()
        mService.marsListener = null
    }
}