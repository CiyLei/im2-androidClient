package com.dj.im.sdk


/**
 * Create by ChenLei on 2020/4/12
 * Describe: 返回结果的枚举
 */
sealed class ResultEnum(val code: Int, val message: String) {
    object Success : ResultEnum(100000, "成功")
    object Error_Request : ResultEnum(100001, "请求错误")
    object Error_Empty : ResultEnum(100002, "服务器列表为空")
}