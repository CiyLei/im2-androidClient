package com.dj.im.sdk.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.dj.im.sdk.entity.ImConversation

/**
 * Create by ChenLei on 2020/12/9
 * Describe: IM会话库操作
 */
@Dao
internal interface ImConversationRoomDao : ImBaseDao<ImConversation> {

    /**
     * 获取会话列表
     */
    @Query("SELECT conversation.*, max(message.createTime) FROM conversation LEFT OUTER JOIN message ON conversation.belongUserName = message.belongUserName AND conversation.`key` = message.conversationKey GROUP BY conversation.belongUserName,Conversation.`key` HAVING conversation.belongAppId = :belongAppId and conversation.belongUserName = :belongUserName ORDER BY createTime DESC")
    fun getConversationList(belongAppId: String, belongUserName: String): MutableList<ImConversation>

    /**
     * 获取会话信息
     */
    @Query("select * from conversation where `key` = :conversationKey and belongAppId = :belongAppId and belongUserName = :belongUserName")
    fun getConversation(
        belongAppId: String,
        belongUserName: String,
        conversationKey: String
    ): ImConversation?
}