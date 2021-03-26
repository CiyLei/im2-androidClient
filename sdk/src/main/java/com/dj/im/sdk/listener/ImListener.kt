package com.dj.im.sdk.listener

import com.dj.im.sdk.convert.message.Message


/**
 * Create by ChenLei on 2020/4/12
 * Describe: 监听连接情况的回调
 */
open class ImListener {

    /**
     * 登录回调
     */
    open fun onLogin(code: Int, message: String) {}

    /**
     * 有消息推送的回调（自己发送消息也会回调）
     */
    open fun onPushMessage(message: Message) {}

    /**
     * 某条消息的发送状态发生了变化
     * @param conversationKey 会话Key
     * @param messageId 消息id
     * @param state 消息状态
     */
    open fun onChangeMessageSendState(conversationKey: String, messageId: Long, state: Int) {}

    /**
     * 消息本地id转为服务器id的回调
     * 发送消息时，消息的id是本地随机生成的，发送成功后会由服务器指定，这是会回调
     */
    open fun onChangeMessageId(oldMessageId: Long, newMessageId: Long) {}

    /**
     * 会话发生了变化
     */
    open fun onChangeConversions() {}

    /**
     * 会话已读监听
     */
    open fun onChangeConversationRead(conversationKey: String, readUserName: String) {}

    /**
     * 离线监听
     */
    open fun onOffline(code: Int, message: String) {};

    /**
     * 用户信息更改回调
     */
    open fun onUserInfoChange(userId: Long) {}

    /**
     * 群信息更改回调
     */
    open fun onGroupInfoChange(groupId: Long) {}

    /**
     * 连接发生变化
     */
    open fun onConnect(isConnect: Boolean) {}

    /**
     * 消息撤回
     */
    open fun onRevokeMessage(conversationKey: String, messageId: Long) {}
}
