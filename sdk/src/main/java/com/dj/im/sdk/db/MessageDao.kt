package com.dj.im.sdk.db

import android.content.Context
import com.dj.im.sdk.entity.message.Message
import com.dj.im.sdk.message.PushMessage
import java.lang.Exception

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
                "insert into Message(userId,id,conversationId,conversationType,type,fromId,toId,data,summary,createTime) values (?,?,?,?,?,?,?,?,?,?)"
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
                    message.createTime
                )
            )
        } catch (e: Exception) {
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
                "insert into Message(userId,id,conversationId,conversationType,type,fromId,toId,data,summary,createTime) values (?,?,?,?,?,?,?,?,?,?)"
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
                    message.createTime
                )
            )
        } catch (e: Exception) {
        } finally {
            writableDatabase.close()
        }
    }
}