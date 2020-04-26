package com.dj.im.sdk.db

import android.arch.persistence.room.*
import com.dj.im.sdk.entity.ImConversation
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.ImUser

/**
 * Create by ChenLei on 2020/4/26
 * Describe: Im数据库Dao
 */
@Dao
interface ImRoomDao {

    /**
     * 获取指定消息之前的所有消息列表
     */
    @Query("SELECT * FROM message WHERE conversationKey = :conversationKey AND userId = :userId AND ( createTime < ( SELECT createTime FROM Message WHERE userId = :userId AND conversationKey = :conversationKey AND id = :messageId ) OR id < :messageId ) ORDER BY createTime DESC, id DESC")
    fun getPreviousAllMessageList(
        userId: Long,
        conversationKey: String,
        messageId: Long
    ): MutableList<ImMessage>

    /**
     * 获取指定消息之前的消息列表
     */
    @Query("SELECT * FROM message WHERE conversationKey = :conversationKey AND userId = :userId AND ( createTime < ( SELECT createTime FROM Message WHERE userId = :userId AND conversationKey = :conversationKey AND id = :messageId ) OR id < :messageId ) ORDER BY createTime DESC, id DESC LIMIT 0,:pageSize")
    fun getPreviousMessageList(
        userId: Long,
        conversationKey: String,
        messageId: Long,
        pageSize: Int
    ): MutableList<ImMessage>

    /**
     * 获取最新的消息
     */
    @Query("SELECT * FROM Message WHERE userId = :userId AND conversationKey = :conversationKey ORDER BY createTime DESC, id DESC LIMIT 0, :pageSize")
    fun getNewMessage(
        userId: Long,
        conversationKey: String,
        pageSize: Int
    ): MutableList<ImMessage>

    /**
     * 插入消息
     */
    @Insert
    fun addPushMessage(message: ImMessage)

    /**
     * 获取消息信息
     */
    @Query("select * from Message where id = :messageId and userId = :userId")
    fun getMessage(userId: Long, messageId: Long): ImMessage?

    /**
     * 更新消息
     */
    @Update
    fun updateMessage(message: ImMessage)

    /**
     * 更新消息
     */
    @Update
    fun updateMessageList(message: List<ImMessage>)

    /**
     * 获取会话列表
     */
    @Query("SELECT conversation.*, message.createTime FROM conversation LEFT OUTER JOIN message ON conversation.userId = message.userId AND conversation.`key` = message.conversationKey GROUP BY conversation.userId,Conversation.`key` HAVING conversation.userId = :userId ORDER BY createTime DESC")
    fun getConversationList(userId: Long): MutableList<ImConversation>

    /**
     * 获取会话信息
     */
    @Query("select * from conversation where `key` = :conversationKey and userId = :userId")
    fun getConversation(userId: Long, conversationKey: String): ImConversation?

    /**
     * 获取会话中最后一条消息
     */
    @Query("SELECT * FROM Message WHERE conversationKey = :conversationKey AND userId = :userId ORDER BY createTime DESC, id DESC LIMIT 0, 1")
    fun getConversationLastMessage(userId: Long, conversationKey: String): ImMessage?

    /**
     * 获取会话中未读的消息
     */
    @Query("select * from message where conversationKey = :conversationKey and userId = :userId and isRead = 0")
    fun getConversationUnReadMessage(userId: Long, conversationKey: String): MutableList<ImMessage>

    /**
     * 添加会话
     */
    @Insert
    fun addConversation(conversation: ImConversation)

    /**
     * 更新会话信息
     */
    @Update
    fun updateConversation(conversation: ImConversation)

    /**
     * 删除会话列表
     */
    @Delete
    fun deleteConversation(conversationList: List<ImConversation>)

    /**
     * 获取用户信息
     */
    @Query("select * from user where id = :id and userId = :userId")
    fun getUser(userId: Long, id: Long): ImUser?

    /**
     * 添加用户
     */
    @Insert
    fun addUser(user: ImUser)

    /**
     * 更新用户
     */
    @Update
    fun updateUser(user: ImUser)
}