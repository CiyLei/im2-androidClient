package com.dj.im.sdk.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.dj.im.sdk.entity.ImMessage

/**
 * Create by ChenLei on 2020/12/9
 * Describe: IM会话库操作
 */
@Dao
internal interface ImMessageRoomDao : ImBaseDao<ImMessage> {

    /**
     * 获取指定消息之前的所有消息列表
     */
    @Query("SELECT * FROM message WHERE conversationKey = :conversationKey AND belongAppId = :belongAppId and belongUserName = :belongUserName AND ( createTime < ( SELECT createTime FROM Message WHERE belongAppId = :belongAppId and belongUserName = :belongUserName AND conversationKey = :conversationKey AND id = :messageId ) OR id < :messageId ) ORDER BY createTime DESC, id DESC")
    fun getPreviousAllMessageList(
        belongAppId: String,
        belongUserName: String,
        conversationKey: String,
        messageId: Long
    ): MutableList<ImMessage>

    /**
     * 获取指定消息之前的消息列表
     */
    @Query("SELECT * FROM message WHERE conversationKey = :conversationKey AND belongAppId = :belongAppId and belongUserName = :belongUserName AND ( createTime < ( SELECT createTime FROM Message WHERE belongAppId = :belongAppId and belongUserName = :belongUserName AND conversationKey = :conversationKey AND id = :messageId ) OR id < :messageId ) ORDER BY createTime DESC, id DESC LIMIT 0,:pageSize")
    fun getPreviousMessageList(
        belongAppId: String,
        belongUserName: String,
        conversationKey: String,
        messageId: Long,
        pageSize: Int
    ): MutableList<ImMessage>

    /**
     * 获取最新的消息
     */
    @Query("SELECT * FROM Message WHERE belongAppId = :belongAppId and belongUserName = :belongUserName AND conversationKey = :conversationKey ORDER BY createTime DESC, id DESC LIMIT 0, :pageSize")
    fun getNewMessage(
        belongAppId: String,
        belongUserName: String,
        conversationKey: String,
        pageSize: Int
    ): MutableList<ImMessage>

    /**
     * 获取消息信息
     */
    @Query("select * from Message where id = :messageId and belongAppId = :belongAppId and belongUserName = :belongUserName")
    fun getMessage(belongAppId: String, belongUserName: String, messageId: Long): ImMessage?

    /**
     * 获取会话中最后一条消息
     */
    @Query("SELECT * FROM Message WHERE conversationKey = :conversationKey AND belongAppId = :belongAppId and belongUserName = :belongUserName ORDER BY createTime DESC, id DESC LIMIT 0, 1")
    fun getConversationLastMessage(belongAppId: String, belongUserName: String, conversationKey: String): ImMessage?
}