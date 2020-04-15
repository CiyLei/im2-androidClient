package com.dj.im.sdk


/**
 * Create by ChenLei on 2020/4/11
 * Describe: 各种常量存放地址
 */
internal object Constant {
    object CMD {
        /**
         * 验证包
         */
        const val AUTH = 1001
        /**
         * 发送消息
         */
        var SEND_MESSAGE = 1004
        /**
         * 推送消息
         */
        var PUSH_MESSAGE = 1005
    }

    object URL {
        const val BASE_URL = "http://192.168.2.116:8081/"
        const val DNS = "/dns/server"
    }

    object ConversationType {
        /**
         * 单聊
         */
        var SINGLE = 0
        /**
         * 群聊
         */
        var GROUP = 1
    }
}