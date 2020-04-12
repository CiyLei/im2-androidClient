package com.dj.im.sdk.entity


/**
 * Create by ChenLei on 2020/4/11
 * Describe: Response封装
 */
data class BaseResponse<T>(val code: String, val msg: String, val data: T, val success: Boolean)
