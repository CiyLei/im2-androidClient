package com.dj.im.sdk.db

import android.content.Context
import android.database.Cursor
import com.dj.im.sdk.Constant
import com.dj.im.sdk.DJIM
import com.dj.im.sdk.conversation.Conversation
import com.dj.im.sdk.conversation.SingleConversation
import com.dj.im.sdk.convert.MessageConvertFactory
import com.dj.im.sdk.entity.message.Message
import com.dj.im.sdk.message.PushMessage
import com.dj.im.server.modules.im.message.PushConversation
import com.dj.im.server.modules.im.message.User
import java.util.*
import kotlin.collections.ArrayList

/**
 * Create by ChenLei on 2020/4/17
 * Describe: Dao层
 */
internal class ConversationDao(context: Context) {
    private val db = ImDB.instance(context)

    /**
     * 添加消息到数据库（推送的）
     */
    fun addPushMessage(userId: Long, message: PushMessage.PushMessageResponse) {
        val messageEntity = Message().apply {
            id = message.id
            conversationId = message.conversationId
            conversationType = message.conversationType
            type = message.type
            fromId = message.fromId
            toId = message.toId
            data = message.data
            summary = message.summary
            createTime = Date(message.createTime)
            state = Message.State.SUCCESS
            // 如果是自己发送的消息，一定是已读
            isRead = if (userId == message.fromId) true else message.isRead
        }
        addMessage(userId, messageEntity)
    }

    /**
     * 添加消息到数据库,如果存在了，那就修改状态为发送成功
     * @param userId 当前用户
     * @param message 发送、接收的消息
     * @param fromUser 消息来源方的消息(只有推送的时候有)
     */
    fun addMessage(userId: Long, message: Message) {
        val writableDatabase = db.writableDatabase
        try {
            val cursor = writableDatabase.rawQuery(
                "select * from Message where userId = ? and id = ?",
                arrayOf(userId.toString(), message.id.toString())
            )
            if (cursor.moveToNext()) {
                // 如果消息存在，更改消息的发送状态
                writableDatabase.execSQL(
                    "update Message set state = ? where userId = ? and id = ?",
                    arrayOf(Message.State.SUCCESS, userId, message.id)
                )
            } else {
                // 如果消息不存在则插入
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
            }
            cursor.close()
        } catch (e: Exception) {
        } finally {
            writableDatabase.close()
        }
    }

    /**
     * 根据推送的消息添加会话（会话有则未读数量加1，没有则创建）
     * @param message 推送消息
     */
    fun addConversationForPushMessage(userId: Long, message: PushMessage.PushMessageResponse) {
        val writableDatabase = db.writableDatabase
        // 判断消息来源是不是自己
        val isSelf = message.fromId == userId
        try {
            val cursor = writableDatabase.rawQuery(
                "select * from Conversation where userId = ? and id = ?", arrayOf(
                    userId.toString(), message.conversationId
                )
            )
            if (cursor.moveToNext()) {
                // 要消息的来源方不是自己才加1
                if (!isSelf) {
                    // 如果会话存在，未读数量加1
                    writableDatabase.execSQL(
                        "update Conversation set unReadCount = unReadCount + 1 where userId = ? and id = ?",
                        arrayOf(userId, message.conversationId)
                    )
                }
            } else {
                // TODO 群聊需要区分
                // 如果会话不存在，创建一个会话
                writableDatabase.execSQL(
                    "insert into Conversation(userId,id,type,unReadCount,tUserId) values (?,?,?,?,?)",
                    arrayOf(
                        userId,
                        message.conversationId,
                        message.conversationType,
                        // 如果是自己发送消息的话，未读数量为0，否则数量为1
                        if (isSelf) 0 else 1,
                        // 如果是自己发送消息的话，id为to，否则是from
                        if (isSelf) message.toId else message.fromId
                    )
                )
            }
            cursor.close()
        } finally {
            writableDatabase.close()
        }
    }

    /**
     * 添加用户信息，如果存在则更新(一般是会话推送的时候保存的)
     */
    fun addUser(userId: Long, userInfo: User.UserResponse) {
        addUser(
            userId,
            com.dj.im.sdk.entity.User(
                userInfo.userId,
                userInfo.userName,
                userInfo.alias,
                userInfo.avatarUrl
            )
        )
    }

    /**
     * 添加用户信息，如果存在则更新(一般是主动发送消息的时候保存的)
     */
    fun addUser(userId: Long, userInfo: com.dj.im.sdk.entity.User) {
        val writableDatabase = db.writableDatabase
        try {
            val cursor = writableDatabase.rawQuery(
                "select * from User where userId = ? and id = ?",
                arrayOf(userId.toString(), userInfo.id.toString())
            )
            if (cursor.moveToNext()) {
                // 用户存在，更新用户信息
                writableDatabase.execSQL(
                    "update User set userName=?,alias=?,avatarUrl=? where userId = ? and id = ?",
                    arrayOf(
                        userInfo.userName,
                        userInfo.alias,
                        userInfo.avatarUrl,
                        userId,
                        userInfo.id
                    )
                )
                cursor.close()
            } else {
                // 用户不存在，插入用户
                writableDatabase.execSQL(
                    "insert into User(userId,id,userName,alias,avatarUrl) values (?,?,?,?,?)",
                    arrayOf(
                        userId,
                        userInfo.id,
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
     * 清除会话缓存
     */
    fun clearConversation(userId: Long) {
        val writableDatabase = db.writableDatabase
        try {
            writableDatabase.execSQL("delete from Conversation where userId = ?", arrayOf(userId))
        } finally {
            writableDatabase.close()
        }
    }

    /**
     * 添加会话信息，如果存在则更新
     */
    fun addConversation(userId: Long, conversation: PushConversation.Conversation) {
        val writableDatabase = db.writableDatabase
        try {
            // 会话不存在，插入会话
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
            addPushMessage(userId, messageResponse)
        }
    }

    /**
     * 获取某个用户的会话信息
     */
    fun getConversations(userId: Long): List<Conversation> {
        val result = ArrayList<Conversation>()
        val readableDatabase = db.readableDatabase
        try {
            val cursor = readableDatabase.rawQuery(
                "SELECT\n" +
                        "\tConversation.id,\n" +
                        "\tConversation.type,\n" +
                        "\tConversation.unReadCount,\n" +
                        "\tUser.id AS userId,\n" +
                        "\tUser.userName AS userName,\n" +
                        "\tUser.alias AS alias,\n" +
                        "\tUser.avatarUrl AS avatarUrl,\n" +
                        "\tMessage.createTime\n" +
                        "FROM\n" +
                        "\tConversation\n" +
                        "\tLEFT OUTER JOIN User ON Conversation.userId = User.userId \n" +
                        "\tAND Conversation.tUserId = User.id \n" +
                        "\tLEFT OUTER JOIN Message ON Conversation.userId = Message.userId \n" +
                        "\tAND Conversation.id = Message.conversationId\n" +
                        "\tGROUP BY Conversation.userId,Conversation.id\n" +
                        "HAVING\n" +
                        "\tConversation.userId = ?\n" +
                        "\tORDER BY createTime DESC",
                arrayOf(userId.toString())
            )
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex("id"))
                val type = cursor.getInt(cursor.getColumnIndex("type"))
                val unReadCount = cursor.getInt(cursor.getColumnIndex("unReadCount"))
                val userId2 = cursor.getLong(cursor.getColumnIndex("userId"))
                val userName = cursor.getString(cursor.getColumnIndex("userName"))
                val alias = cursor.getString(cursor.getColumnIndex("alias"))
                val avatarUrl = cursor.getString(cursor.getColumnIndex("avatarUrl"))
                // 单聊
                if (type == Constant.ConversationType.SINGLE) {
                    val singleConversation = SingleConversation(
                        com.dj.im.sdk.entity.User(
                            userId2,
                            userName,
                            alias,
                            avatarUrl
                        )
                    )
                    singleConversation.unReadCount = unReadCount
                    result.add(singleConversation)
                }
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            readableDatabase.close()
        }
        return result
    }

    /**
     * 获取某个会话的最后一条消息
     */
    fun getLastMessage(userId: Long, conversationId: String): Message? {
        val readableDatabase = db.readableDatabase
        try {
            val cursor = readableDatabase.rawQuery(
                "SELECT * FROM Message WHERE userId = ? AND conversationId = ? ORDER BY createTime DESC LIMIT 0, 1",
                arrayOf(userId.toString(), conversationId)
            )
            if (cursor.moveToNext()) {
                val message = getMessageFromCursor(cursor)
                cursor.close()
                // 将消息转换为对应的类型
                return MessageConvertFactory.convert(message)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            readableDatabase.close()
        }
        return null
    }

    /**
     * 从游标中读取消息信息
     */
    private fun getMessageFromCursor(cursor: Cursor): Message {
        val message = Message()
        message.id = cursor.getLong(cursor.getColumnIndex("id"))
        message.conversationId = cursor.getString(cursor.getColumnIndex("conversationId"))
        message.conversationType = cursor.getInt(cursor.getColumnIndex("conversationType"))
        message.type = cursor.getInt(cursor.getColumnIndex("type"))
        message.fromId = cursor.getLong(cursor.getColumnIndex("fromId"))
        message.toId = cursor.getLong(cursor.getColumnIndex("toId"))
        message.data = cursor.getString(cursor.getColumnIndex("data"))
        message.summary = cursor.getString(cursor.getColumnIndex("summary"))
        message.createTime = Date(cursor.getLong(cursor.getColumnIndex("createTime")))
        message.state = cursor.getInt(cursor.getColumnIndex("state"))
        message.isRead = cursor.getInt(cursor.getColumnIndex("isRead")) == 1
        return message
    }

    /**
     * 根据消息id获取消息
     */
    fun getMessageForId(userId: Long, messageId: Long): Message? {
        val readableDatabase = db.readableDatabase
        try {
            val cursor = readableDatabase.rawQuery(
                "select * from Message where userId = ? and id = ?",
                arrayOf(userId.toString(), messageId.toString())
            )
            if (cursor.moveToNext()) {
                val message = getMessageFromCursor(cursor)
                cursor.close()
                return MessageConvertFactory.convert(message)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            readableDatabase.close()
        }
        return null
    }

    /**
     * 获取最新的消息列表（最新的在前面）
     */
    fun getNewestMessages(userId: Long, conversationId: String, pageSize: Int): List<Message> {
        val result = ArrayList<Message>()
        val readableDatabase = db.readableDatabase
        try {
            val cursor = readableDatabase.rawQuery(
                "SELECT * FROM Message WHERE userId = ? AND conversationId = ? ORDER BY createTime DESC LIMIT 0, ?",
                arrayOf(userId.toString(), conversationId, pageSize.toString())
            )
            while (cursor.moveToNext()) {
                val message = getMessageFromCursor(cursor)
                result.add(MessageConvertFactory.convert(message))
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            readableDatabase.close()
        }
        return result
    }

    /**
     * 获取用户信息
     * @param userId 当前用户
     * @param id 查询的用户id
     */
    fun getUser(userId: Long, id: Long): com.dj.im.sdk.entity.User? {
        val readableDatabase = db.readableDatabase
        try {
            val cursor = readableDatabase.rawQuery(
                "select * from User where userId = ? and id = ?",
                arrayOf(userId.toString(), id.toString())
            )
            if (cursor.moveToNext()) {
                val uid = cursor.getLong(cursor.getColumnIndex("id"))
                val userName = cursor.getString(cursor.getColumnIndex("userName"))
                val alias = cursor.getString(cursor.getColumnIndex("alias"))
                val avatarUrl = cursor.getString(cursor.getColumnIndex("avatarUrl"))
                cursor.close()
                return com.dj.im.sdk.entity.User(uid, userName, alias, avatarUrl)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            readableDatabase.close()
        }
        return null
    }
}