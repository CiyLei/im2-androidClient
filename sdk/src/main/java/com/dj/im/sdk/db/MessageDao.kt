package com.dj.im.sdk.db

import android.content.Context
import com.dj.im.sdk.entity.message.Message
import com.dj.im.sdk.message.PushMessage
import com.dj.im.server.modules.im.message.PushConversation
import com.dj.im.server.modules.im.message.User
import java.util.*

/**
 * Create by ChenLei on 2020/4/17
 * Describe: 消息Dao层
 */
internal class MessageDao(context: Context) {
    private val db = ImDB.instance(context)

    /**
     * 添加消息到数据库
     */
    fun addMessage(userId: Long, message: PushMessage.PushMessageResponse) {
        val writableDatabase = db.writableDatabase
        try {
            writableDatabase.execSQL(
                "insert into Message(userId,id,conversationId,conversationType,type,fromId,toId,data,summary,createTime,isRead) values (?,?,?,?,?,?,?,?,?,?,?)"
                , arrayOf(
                    userId,
                    message.id,
                    message.conversationId,
                    message.conversationType,
                    message.type,
                    message.fromId,
                    message.toId,
                    message.data,
                    message.summary,
                    Date(message.createTime),
                    message.isRead
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            writableDatabase.close()
        }
    }

    /**
     * 添加消息到数据库
     */
    fun addMessage(userId: Long, message: Message) {
        val writableDatabase = db.writableDatabase
        try {
            writableDatabase.execSQL(
                "insert into Message(userId,id,conversationId,conversationType,type,fromId,toId,data,summary,createTime,state,isRead) values (?,?,?,?,?,?,?,?,?,?,?,?)"
                , arrayOf(
                    userId,
                    message.id,
                    message.conversationId,
                    message.conversationType,
                    message.type,
                    message.fromId,
                    message.toId,
                    message.data,
                    message.summary,
                    message.createTime,
                    message.state,
                    message.isRead
                )
            )
        } catch (e: Exception) {
        } finally {
            writableDatabase.close()
        }
    }

    /**
     * 添加用户信息，如果存在则更新
     */
    fun addUser(userId: Long, userInfo: User.UserResponse) {
        val writableDatabase = db.writableDatabase
        try {
            val user = writableDatabase.rawQuery(
                "select * from User where userId = ? and id = ?",
                arrayOf(userId.toString(), userInfo.userId.toString())
            )
            if (user.moveToNext()) {
                // 用户存在，更新用户信息
                writableDatabase.execSQL(
                    "update User set userName=?,alias=?,avatarUrl=? where userId = ? and id = ?",
                    arrayOf(
                        userInfo.userName,
                        userInfo.alias,
                        userInfo.avatarUrl,
                        userId,
                        userInfo.userId
                    )
                )
            } else {
                // 用户不存在，插入用户
                writableDatabase.execSQL(
                    "insert into User(userId,id,userName,alias,avatarUrl) values (?,?,?,?,?)",
                    arrayOf(
                        userId,
                        userInfo.userId,
                        userInfo.userName,
                        userInfo.alias,
                        userInfo.avatarUrl
                    )
                )
            }
        } catch (e: Exception) {
        } finally {
            writableDatabase.close()
        }
    }

    /**
     * 添加会话信息
     */
    fun addConversation(userId: Long, conversation: PushConversation.Conversation) {
        val writableDatabase = db.writableDatabase
        try {
            writableDatabase.execSQL(
                "insert into Conversation(userId,id,type,unReadCount,tUserId) values (?,?,?,?,?)",
                arrayOf(
                    userId,
                    conversation.conversationId,
                    conversation.conversationType,
                    conversation.unReadCount,
                    conversation.toUserInfo.userId
                )
            )
        } catch (e: Exception) {
        } finally {
            writableDatabase.close()
        }
        for (messageResponse in conversation.messagesList) {
            addMessage(userId, messageResponse)
        }
    }
}