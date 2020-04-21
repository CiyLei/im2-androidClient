package com.dj.im.sdk.db

/**
 * Create by ChenLei on 2020/4/17
 * Describe: Dao层
 */
//internal class ConversationDao(context: Context) {
//    private val db = ImDB.instance(context)
//
//    /**
//     * 添加消息到数据库（推送的）
//     */
//    @Synchronized
//    fun addPushMessage(userId: Long, message: PrPushMessage.PushMessageResponse) {
//        val messageEntity = ImMessage().apply {
//            id = message.id
//            conversationId = message.conversationId
//            conversationType = message.conversationType
//            type = message.type
//            fromId = message.fromId
//            toId = message.toId
//            data = message.data
//            summary = message.summary
//            createTime = Date(message.createTime)
//            state = ImMessage.State.SUCCESS
//            // 如果是自己发送的消息，一定是已读
//            isRead = message.isRead
//        }
//        addMessage(userId, messageEntity)
//    }
//
//    /**
//     * 添加消息到数据库,如果存在了，那就修改状态为发送成功
//     * @param userId 当前用户
//     * @param message 发送、接收的消息
//     * @param fromUser 消息来源方的消息(只有推送的时候有)
//     */
//    @Synchronized
//    fun addMessage(userId: Long, message: ImMessage) {
//        val writableDatabase = db.writableDatabase
//        try {
//            val cursor = writableDatabase.rawQuery(
//                "select * from Message where userId = ? and id = ?",
//                arrayOf(userId.toString(), message.id.toString())
//            )
//            if (cursor.moveToNext()) {
//                // 如果消息存在，需要更改的字段：发送状态、已读状态
//                writableDatabase.execSQL(
//                    "update Message set state = ?,isRead = ? where userId = ? and id = ?",
//                    arrayOf(ImMessage.State.SUCCESS, message.isRead, userId, message.id)
//                )
//            } else {
//                // 如果消息不存在则插入
//                writableDatabase.execSQL(
//                    "insert into Message(userId,id,conversationId,conversationType,type,fromId,toId,data,summary,createTime,state,isRead) values (?,?,?,?,?,?,?,?,?,?,?,?)"
//                    , arrayOf(
//                        userId,
//                        message.id,
//                        message.conversationId,
//                        message.conversationType,
//                        message.type,
//                        message.fromId,
//                        message.toId,
//                        message.data,
//                        message.summary,
//                        message.createTime,
//                        message.state,
//                        message.isRead
//                    )
//                )
//            }
//            cursor.close()
//        } catch (e: Exception) {
//        } finally {
//            writableDatabase.close()
//        }
//    }
//
//    /**
//     * 根据推送的消息添加会话（会话有则未读数量加1，没有则创建）
//     * @param message 推送消息
//     */
//    @Synchronized
//    fun addConversationForPushMessage(userId: Long, message: PrPushMessage.PushMessageResponse) {
//        val writableDatabase = db.writableDatabase
//        // 判断消息来源是不是自己
//        val isSelf = message.fromId == userId
//        try {
//            val cursor = writableDatabase.rawQuery(
//                "select * from Conversation where userId = ? and id = ?", arrayOf(
//                    userId.toString(), message.conversationId
//                )
//            )
//            if (cursor.moveToNext()) {
//                // 要消息的来源方不是自己才加1
//                if (!isSelf) {
//                    // 如果会话存在，未读数量加1
//                    writableDatabase.execSQL(
//                        "update Conversation set unReadCount = unReadCount + 1 where userId = ? and id = ?",
//                        arrayOf(userId, message.conversationId)
//                    )
//                }
//            } else {
//                // TODO 群聊需要区分
//                // 如果会话不存在，创建一个会话
//                writableDatabase.execSQL(
//                    "insert into Conversation(userId,id,type,unReadCount,tUserId) values (?,?,?,?,?)",
//                    arrayOf(
//                        userId,
//                        message.conversationId,
//                        message.conversationType,
//                        // 如果是自己发送消息的话，未读数量为0，否则数量为1
//                        if (isSelf) 0 else 1,
//                        // 如果是自己发送消息的话，id为to，否则是from
//                        if (isSelf) message.toId else message.fromId
//                    )
//                )
//            }
//            cursor.close()
//        } finally {
//            writableDatabase.close()
//        }
//    }
//
//    /**
//     * 添加用户信息，如果存在则更新(一般是会话推送的时候保存的)
//     */
//    @Synchronized
//    fun addUser(userId: Long, userInfo: PrUser.UserResponse) {
//        addUser(
//            userId, ImUser(userInfo.userId, userInfo.userName, userInfo.alias, userInfo.avatarUrl)
//        )
//    }
//
//    /**
//     * 添加用户信息，如果存在则更新(一般是主动发送消息的时候保存的)
//     */
//    @Synchronized
//    fun addUser(userId: Long, userInfo: ImUser) {
//        val writableDatabase = db.writableDatabase
//        try {
//            val cursor = writableDatabase.rawQuery(
//                "select * from User where userId = ? and id = ?",
//                arrayOf(userId.toString(), userInfo.id.toString())
//            )
//            if (cursor.moveToNext()) {
//                // 用户存在，更新用户信息
//                writableDatabase.execSQL(
//                    "update User set userName=?,alias=?,avatarUrl=? where userId = ? and id = ?",
//                    arrayOf(
//                        userInfo.userName,
//                        userInfo.alias,
//                        userInfo.avatarUrl,
//                        userId,
//                        userInfo.id
//                    )
//                )
//            } else {
//                // 用户不存在，插入用户
//                writableDatabase.execSQL(
//                    "insert into User(userId,id,userName,alias,avatarUrl) values (?,?,?,?,?)",
//                    arrayOf(
//                        userId,
//                        userInfo.id,
//                        userInfo.userName,
//                        userInfo.alias,
//                        userInfo.avatarUrl
//                    )
//                )
//            }
//            cursor.close()
//        } catch (e: Exception) {
//        } finally {
//            writableDatabase.close()
//        }
//    }
//
//    /**
//     * 清除会话缓存
//     */
//    @Synchronized
//    fun clearConversation(userId: Long) {
//        val writableDatabase = db.writableDatabase
//        try {
//            writableDatabase.execSQL("delete from Conversation where userId = ?", arrayOf(userId))
//        } finally {
//            writableDatabase.close()
//        }
//    }
//
//    /**
//     * 添加会话信息，如果存在则更新
//     */
//    @Synchronized
//    fun addConversation(userId: Long, conversation: PrPushConversation.Conversation) {
//        val writableDatabase = db.writableDatabase
//        try {
//            // 会话不存在，插入会话
//            writableDatabase.execSQL(
//                "insert into Conversation(userId,id,type,unReadCount,tUserId) values (?,?,?,?,?)",
//                arrayOf(
//                    userId,
//                    conversation.conversationId,
//                    conversation.conversationType,
//                    conversation.unReadCount,
//                    conversation.toUserInfo.userId
//                )
//            )
//        } catch (e: Exception) {
//        } finally {
//            writableDatabase.close()
//        }
//        for (messageResponse in conversation.messagesList) {
//            addPushMessage(userId, messageResponse)
//        }
//    }
//
//    /**
//     * 获取某个用户的会话信息
//     */
//    @Synchronized
//    fun getConversations(userId: Long): List<Conversation> {
//        val result = ArrayList<Conversation>()
//        val readableDatabase = db.readableDatabase
//        try {
//            val cursor = readableDatabase.rawQuery(
//                "SELECT\n" +
//                        "\tConversation.id,\n" +
//                        "\tConversation.type,\n" +
//                        "\tConversation.unReadCount,\n" +
//                        "\tUser.id AS userId,\n" +
//                        "\tUser.userName AS userName,\n" +
//                        "\tUser.alias AS alias,\n" +
//                        "\tUser.avatarUrl AS avatarUrl,\n" +
//                        "\tMessage.createTime\n" +
//                        "FROM\n" +
//                        "\tConversation\n" +
//                        "\tLEFT OUTER JOIN User ON Conversation.userId = User.userId \n" +
//                        "\tAND Conversation.tUserId = User.id \n" +
//                        "\tLEFT OUTER JOIN Message ON Conversation.userId = Message.userId \n" +
//                        "\tAND Conversation.id = Message.conversationId\n" +
//                        "\tGROUP BY Conversation.userId,Conversation.id\n" +
//                        "HAVING\n" +
//                        "\tConversation.userId = ?\n" +
//                        "\tORDER BY createTime DESC",
//                arrayOf(userId.toString())
//            )
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(cursor.getColumnIndex("id"))
//                val type = cursor.getInt(cursor.getColumnIndex("type"))
//                val unReadCount = cursor.getInt(cursor.getColumnIndex("unReadCount"))
//                val userId2 = cursor.getLong(cursor.getColumnIndex("userId"))
//                val userName = cursor.getString(cursor.getColumnIndex("userName"))
//                val alias = cursor.getString(cursor.getColumnIndex("alias"))
//                val avatarUrl = cursor.getString(cursor.getColumnIndex("avatarUrl"))
//                // 单聊
//                if (type == Constant.ConversationType.SINGLE) {
//                    val singleConversation =
//                        SingleConversation(ImUser(userId2, userName, alias, avatarUrl))
//                    singleConversation.unReadCount = unReadCount
//                    result.add(singleConversation)
//                }
//            }
//            cursor.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            readableDatabase.close()
//        }
//        return result
//    }
//
//    /**
//     * 获取某个会话的最后一条消息
//     */
//    @Synchronized
//    fun getLastMessage(userId: Long, conversationId: String): ImMessage? {
//        val readableDatabase = db.readableDatabase
//        try {
//            val cursor = readableDatabase.rawQuery(
//                "SELECT * FROM Message WHERE userId = ? AND conversationId = ? ORDER BY createTime DESC, id DESC LIMIT 0, 1",
//                arrayOf(userId.toString(), conversationId)
//            )
//            if (cursor.moveToNext()) {
//                val message = getMessageFromCursor(cursor)
//                cursor.close()
//                // 将消息转换为对应的类型
//                return MessageConvertFactory.convert(message)
//            }
//            cursor.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            readableDatabase.close()
//        }
//        return null
//    }
//
//    /**
//     * 从游标中读取消息信息
//     */
//    @Synchronized
//    private fun getMessageFromCursor(cursor: Cursor): ImMessage {
//        val message = ImMessage()
//        message.id = cursor.getLong(cursor.getColumnIndex("id"))
//        message.conversationId = cursor.getString(cursor.getColumnIndex("conversationId"))
//        message.conversationType = cursor.getInt(cursor.getColumnIndex("conversationType"))
//        message.type = cursor.getInt(cursor.getColumnIndex("type"))
//        message.fromId = cursor.getLong(cursor.getColumnIndex("fromId"))
//        message.toId = cursor.getLong(cursor.getColumnIndex("toId"))
//        message.data = cursor.getString(cursor.getColumnIndex("data"))
//        message.summary = cursor.getString(cursor.getColumnIndex("summary"))
//        message.createTime = Date(cursor.getLong(cursor.getColumnIndex("createTime")))
//        message.state = cursor.getInt(cursor.getColumnIndex("state"))
//        message.isRead = cursor.getInt(cursor.getColumnIndex("isRead")) == 1
//        return message
//    }
//
//    /**
//     * 根据消息id获取消息
//     */
//    @Synchronized
//    fun getMessageForId(userId: Long, messageId: Long): ImMessage? {
//        val readableDatabase = db.readableDatabase
//        try {
//            val cursor = readableDatabase.rawQuery(
//                "select * from Message where userId = ? and id = ?",
//                arrayOf(userId.toString(), messageId.toString())
//            )
//            if (cursor.moveToNext()) {
//                val message = getMessageFromCursor(cursor)
//                cursor.close()
//                return MessageConvertFactory.convert(message)
//            }
//            cursor.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            readableDatabase.close()
//        }
//        return null
//    }
//
//    /**
//     * 获取最新的消息列表（最新的在前面）
//     */
//    @Synchronized
//    fun getNewestMessages(userId: Long, conversationId: String, pageSize: Int): List<ImMessage> {
//        val result = ArrayList<ImMessage>()
//        val readableDatabase = db.readableDatabase
//        try {
//            val cursor = readableDatabase.rawQuery(
//                "SELECT * FROM Message WHERE userId = ? AND conversationId = ? ORDER BY createTime DESC, id DESC LIMIT 0, ?",
//                arrayOf(userId.toString(), conversationId, pageSize.toString())
//            )
//            while (cursor.moveToNext()) {
//                val message = getMessageFromCursor(cursor)
//                result.add(MessageConvertFactory.convert(message))
//            }
//            cursor.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            readableDatabase.close()
//        }
//        return result
//    }
//
//    /**
//     * 获取用户信息
//     * @param userId 当前用户
//     * @param id 查询的用户id
//     */
//    @Synchronized
//    fun getUser(userId: Long, id: Long): ImUser? {
//        val readableDatabase = db.readableDatabase
//        try {
//            val cursor = readableDatabase.rawQuery(
//                "select * from User where userId = ? and id = ?",
//                arrayOf(userId.toString(), id.toString())
//            )
//            if (cursor.moveToNext()) {
//                val uid = cursor.getLong(cursor.getColumnIndex("id"))
//                val userName = cursor.getString(cursor.getColumnIndex("userName"))
//                val alias = cursor.getString(cursor.getColumnIndex("alias"))
//                val avatarUrl = cursor.getString(cursor.getColumnIndex("avatarUrl"))
//                cursor.close()
//                return ImUser(uid, userName, alias, avatarUrl)
//            }
//            cursor.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            readableDatabase.close()
//        }
//        return null
//    }
//
//    /**
//     * 清空一个会话的未读数量
//     * 自己查看了这个会话
//     */
//    @Synchronized
//    fun clearConversationUnReadCount(userId: Long, conversationId: String) {
//        val writableDatabase = db.writableDatabase
//        try {
//            writableDatabase.execSQL(
//                "update Conversation set unReadCount = 0 where userId = ? and id = ?",
//                arrayOf(userId, conversationId)
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            writableDatabase.close()
//        }
//    }
//
//    /**
//     * 已读一个会话的所有消息
//     * 被人查看了这个会话
//     */
//    @Synchronized
//    fun readConversationMessage(userId: Long, conversationId: String) {
//        val writableDatabase = db.writableDatabase
//        try {
//            writableDatabase.execSQL(
//                "update Message set isRead = 1 where userId = ? and conversationId = ?",
//                arrayOf(userId, conversationId)
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            writableDatabase.close()
//        }
//    }
//
//    /**
//     * 获取指定消息之前的消息列表（即读取历史消息）
//     * @param pageSize 如果等于-1就查询全部
//     */
//    @Synchronized
//    fun getHistoryMessage(
//        userId: Long,
//        conversationId: String,
//        messageId: Long,
//        pageSize: Int = -1
//    ): List<ImMessage> {
//        val result = ArrayList<ImMessage>()
//        val readableDatabase = db.readableDatabase
//        try {
//            var cursor: Cursor? = null
//            if (pageSize == -1) {
//                // 查询全部
//                cursor = readableDatabase.rawQuery(
//                    "SELECT\n" +
//                            "\t* \n" +
//                            "FROM\n" +
//                            "\tMessage \n" +
//                            "WHERE\n" +
//                            "\tuserId = ? \n" +
//                            "\tAND conversationId = ? \n" +
//                            "\tAND ( createTime < ( SELECT createTime FROM Message WHERE userId = ? AND conversationId = ? AND id = ? ) OR id < ? ) \n" +
//                            "ORDER BY\n" +
//                            "\tcreateTime DESC,\n" +
//                            "\tid DESC", arrayOf(
//                        userId.toString(),
//                        conversationId,
//                        userId.toString(),
//                        conversationId,
//                        messageId.toString(),
//                        messageId.toString()
//                    )
//                )
//            } else {
//                cursor = readableDatabase.rawQuery(
//                    "SELECT\n" +
//                            "\t* \n" +
//                            "FROM\n" +
//                            "\tMessage \n" +
//                            "WHERE\n" +
//                            "\tuserId = ? \n" +
//                            "\tAND conversationId = ? \n" +
//                            "\tAND ( createTime < ( SELECT createTime FROM Message WHERE userId = ? AND conversationId = ? AND id = ? ) OR id < ? ) \n" +
//                            "ORDER BY\n" +
//                            "\tcreateTime DESC,\n" +
//                            "\tid DESC \n" +
//                            "\tLIMIT 0,?", arrayOf(
//                        userId.toString(),
//                        conversationId,
//                        userId.toString(),
//                        conversationId,
//                        messageId.toString(),
//                        messageId.toString(),
//                        pageSize.toString()
//                    )
//                )
//            }
//            while (cursor!!.moveToNext()) {
//                val message = getMessageFromCursor(cursor)
//                result.add(MessageConvertFactory.convert(message))
//            }
//            cursor.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            readableDatabase.close()
//        }
//        return result
//    }
//}