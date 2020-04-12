package com.dj.im.sdk.entity


/**
 * Create by ChenLei on 2020/4/11
 * Describe: 服务连接情况
 */
data class ServerSituationEntity(
    /**
     * 所有端口
     */
    val ports: List<Int>,
    /**
     * 推荐的服务器
     */
    val recommend: String,
    /**
     * 所有服务器
     */
    val all: List<String>
)
