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
         * 下线
         */
        var OFFLINE = 1002

        /**
         * 发送消息
         */
        const val SEND_MESSAGE = 1004

        /**
         * 推送消息
         */
        const val PUSH_MESSAGE = 1005

        /**
         * 推送会话
         */
        var PUSH_CONVERSATION = 1006

        /**
         * 已读会话
         */
        var READ_CONVERSATION = 1007

        /**
         * 推送已读会话
         */
        var PUSH_READ_CONVERSATION = 1008

        /**
         * 获取历史消息
         */
        var GET_HISTORY_MESSAGE = 1009
    }

    object URL {
        const val BASE_URL = "http://192.168.2.116:8081/"
        const val DNS = "/dns/server"
        const val UPLOAD = "/upload"
    }

    /**
     * 会话类型
     */
    object ConversationType {
        /**
         * 单聊
         */
        const val SINGLE = 0

        /**
         * 群聊
         */
        const val GROUP = 1
    }
}