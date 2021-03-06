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

        /**
         * 获取用户信息
         */
        var GET_USER_INFO = 1010

        /**
         * 获取群信息
         */
        var GET_GROUP_INFO = 1011

        /**
         * 请求撤回消息
         */
        var REVOKE_MESSAGE = 1012

        /**
         * 推送撤回消息
         */
        var PUSH_REVOKE_MESSAGE = 1013
    }

    object URL {
        const val BASE_URL = "http://192.168.77.43:8081"
        const val FILE_URL = "http://172.17.0.55/"
        const val DNS = "/dns/server"
        const val UPLOAD = "/upload"
        const val DOWNLOAD = "/download"
        const val IM_MESSAGE = "/im/message"
        const val IM_GROUP = "/im/group"
        const val IM_USER = "/im/user"
        const val GET_HISTORY_MESSAGE_LIST = "$IM_MESSAGE/historyMessageList"
        const val GET_GROUP_INFO_BY_IDS = "$IM_GROUP/getGroupInfoByIds"
        const val GET_USER_INFO_BY_IDS = "$IM_USER/getUserInfoByIds"
        const val GET_USER_INFO_BY_NAMES = "$IM_USER/getUserInfoByNames"
        const val GET_USER_INFO_BY_CONVERSATION_KEY = "$IM_USER/getUserInfoByConversationKey"
        const val LOGOUT = "$IM_USER/logout"
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

    /**
     * 保存在数据库中的配置数据key
     */
    object Key {
        /**
         * 登录token
         */
        const val TOKEN = "token"

        /**
         * 最后一次登录的用户信息
         */
        const val LAST_LOGIN_USER = "lastLoginUser"
    }

    /**
     * 消息内容（data）的最大长度
     */
    const val MESSAGE_DATA_MAX_LENGTH = 15000

    /**
     * 离线是读取历史消息的条数
     */
    const val OFFLINE_READ_HISTORY_MESSAGE_COUNT = 100
}