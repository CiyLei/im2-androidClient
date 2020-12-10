package com.dj.im.sdk.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.dj.im.sdk.entity.UnReadMessage

/**
 * Create by ChenLei on 2020/12/9
 * Describe: IM未读库操作
 */
@Dao
internal interface ImUnReadRoomDao : ImBaseDao<UnReadMessage> {
    /**
     * 根据消息，获取所有未读用户
     */
    @Query("select * from UnReadMessage where belongAppId = :belongAppId and belongUserName = :belongUserName and messageId = :messageId")
    fun getAllUnReadListForMessageId(
        belongAppId: String,
        belongUserName: String,
        messageId: Long
    ): List<UnReadMessage>

    /**
     * 获取某个会话下所有某人的未读的消息
     */
    @Query("SELECT u.* FROM ( SELECT * FROM Conversation WHERE belongAppId = :belongAppId and belongUserName = :belongUserName AND `key` = :conversationKey ) c LEFT JOIN Message m ON c.`key` = m.conversationKey LEFT JOIN UnReadMessage u ON m.id = u.messageId WHERE u.unReadUserName = :readUserName")
    fun getAllUnReadListOnConversation(
        belongAppId: String,
        belongUserName: String,
        conversationKey: String,
        readUserName: String
    ): List<UnReadMessage>

    /**
     * 查询某个消息的未读情况
     */
    @Query("select * from UnReadMessage where belongAppId = :belongAppId and belongUserName = :belongUserName and messageId = :messageId")
    fun getUnReadList(
        belongAppId: String,
        belongUserName: String,
        messageId: Long
    ): List<UnReadMessage>
}