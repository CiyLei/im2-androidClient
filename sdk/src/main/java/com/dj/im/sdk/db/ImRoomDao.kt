package com.dj.im.sdk.db

import android.arch.persistence.room.*
import com.dj.im.sdk.entity.*

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
    @Query("SELECT conversation.*, max(message.createTime) FROM conversation LEFT OUTER JOIN message ON conversation.userId = message.userId AND conversation.`key` = message.conversationKey GROUP BY conversation.userId,Conversation.`key` HAVING conversation.userId = :userId ORDER BY createTime DESC")
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
//    @Query("select * from message where conversationKey = :conversationKey and userId = :userId and isRead = 0")
//    fun getConversationUnReadMessage(userId: Long, conversationKey: String): MutableList<ImMessage>

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

    /**
     * 添加用户消息未读
     */
    @Insert
    fun addUnReadMessageUser(unReadUserList: List<UnReadMessage>)

    /**
     * 添加用户消息未读
     */
    @Insert
    fun addUnReadMessageUser(unReadUser: UnReadMessage)

    /**
     * 查询某个消息的未读情况
     */
    @Query("select * from UnReadMessage where userId = :userId and messageId = :messageId")
    fun getUnReadList(userId: Long, messageId: Long): List<UnReadMessage>

    /**
     * 获取所有的未读信息
     */
    @Query("select * from UnReadMessage where userId = :userId")
    fun getAllUnReadList(userId: Long): List<UnReadMessage>

    /**
     * 获取某个会话下所有某人的未读的消息
     */
    @Query("SELECT u.* FROM ( SELECT * FROM Conversation WHERE userId = :userId AND `key` = :conversationKey ) c LEFT JOIN Message m ON c.`key` = m.conversationKey LEFT JOIN UnReadMessage u ON m.id = u.   messageId WHERE u.unReadUserId = :readUserId")
    fun getAllUnReadListOnConversation(
        userId: Long,
        conversationKey: String,
        readUserId: Long
    ): List<UnReadMessage>

    /**
     * 根据消息，获取所有未读用户
     */
    @Query("select * from UnReadMessage where userId = :userId and messageId = :messageId")
    fun getAllUnReadListForMessageId(userId: Long, messageId: Long): List<UnReadMessage>

    /**
     * 删除用户消息未读
     */
    @Delete
    fun deleteUnReadMessageUser(unReadUserList: List<UnReadMessage>)

    /**
     * 添加群信息
     */
    @Insert
    fun addGroup(group: ImGroup)

    /**
     * 更新群信息
     */
    @Update
    fun updateGroup(group: ImGroup)

    /**
     * 查询群消息
     */
    @Query("select * from `Group` where userId = :userId and id = :groupId")
    fun getGroupInfo(userId: Long, groupId: Long): ImGroup?

    /**
     * 添加群的用户列表
     */
    @Insert
    fun addUserGroup(userGroupList: List<UserGroupEntity>)

    /**
     * 获取群的用户列表
     */
    @Query("select * from UserGroup where userId = :userId and groupId = :groupId ")
    fun getUserGroupList(userId: Long, groupId: Long): List<UserGroupEntity>

    /**
     * 删除群的用户列表
     */
    @Delete
    fun deleteUserGroup(userGroupList: List<UserGroupEntity>)
}