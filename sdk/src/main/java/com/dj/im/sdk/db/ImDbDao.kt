package com.dj.im.sdk.db

import android.content.Context
import com.dj.im.sdk.IDBDao
import com.dj.im.sdk.entity.*
import com.dj.im.sdk.proto.PrPushConversation
import com.dj.im.sdk.utils.MessageConvertUtil
import kotlin.collections.ArrayList

/**
 * Create by ChenLei on 2020/4/20
 * Describe: im数据库Dao层
 */
class ImDbDao(context: Context) : IDBDao.Stub() {

    private val roomDao = ImRoomDatabase.instance(context).imRoomDao()

    /**
     * 获取指定消息之前的消息列表（即读取历史消息）
     * @param pageSize 如果等于-1就查询全部
     */
    @Synchronized
    override fun getHistoryMessage(
        userId: Long,
        conversationKey: String,
        messageId: Long,
        pageSize: Int
    ): MutableList<ImMessage> {
        if (pageSize > 0) {
            return roomDao.getPreviousMessageList(userId, conversationKey, messageId, pageSize)
        }
        return roomDao.getPreviousAllMessageList(userId, conversationKey, messageId)
    }

    /**
     * 添加消息到数据库
     */
    @Synchronized
    override fun addPushMessage(userId: Long, message: ImMessage) {
        val imMessage = roomDao.getMessage(userId, message.id)
        message.userId = userId
        if (imMessage == null) {
            roomDao.addPushMessage(message)
        } else {
            roomDao.updateMessage(message)
        }
    }

    /**
     * 清除会话缓存
     */
    @Synchronized
    override fun clearConversation(userId: Long) {
        val conversationList = roomDao.getConversationList(userId)
        roomDao.deleteConversation(conversationList)
        // 清空所有未读
//        roomDao.deleteUnReadMessageUser(roomDao.getAllUnReadList(userId))
    }

    /**
     * 获取某个用户的会话信息
     */
    @Synchronized
    override fun getConversations(userId: Long): MutableList<ImConversation> {
        return roomDao.getConversationList(userId)
    }

    /**
     * 获取用户信息
     * @param userId 当前用户
     * @param id 查询的用户id
     */
    @Synchronized
    override fun getUser(userId: Long, id: Long): ImUser? {
        return roomDao.getUser(userId, id)
    }

    /**
     * 获取某个会话的最后一条消息
     */
    @Synchronized
    override fun getLastMessage(userId: Long, conversationKey: String): ImMessage? {
        return roomDao.getConversationLastMessage(userId, conversationKey)
    }

    /**
     * 添加会话信息，如果存在则更新
     */
    @Synchronized
    fun addConversation(userId: Long, conversation: PrPushConversation.Conversation) {
        roomDao.addConversation(
            ImConversation(
                conversation.conversationKey,
                conversation.conversationType,
                conversation.unReadCount,
                if (conversation.conversationType == ImConversation.Type.SINGLE)
                    conversation.otherSideUserInfo.userId
                else
                    conversation.groupInfo.groupId,
                userId
            )
        )
        conversation.messagesList.forEach {
            addPushMessage(userId, MessageConvertUtil.prPushMessage2ImMessage(it))
            roomDao.deleteUnReadMessageUser(roomDao.getAllUnReadListForMessageId(userId, it.id))
            // 保存未读信息
            val unReadUserIdList = it.unReadUserIdListList
            addUnReadMessage(userId, ArrayList(unReadUserIdList.map { m ->
                UnReadMessage(userId, it.id, m)
            }))
        }
    }

    /**
     * 获取最新的消息列表（最新的在前面）
     */
    @Synchronized
    override fun getNewestMessages(
        userId: Long,
        conversationKey: String,
        pageSize: Int
    ): MutableList<ImMessage> {
        return roomDao.getNewMessage(userId, conversationKey, pageSize)
    }

    /**
     * 根据消息id获取消息
     */
    @Synchronized
    override fun getMessageForId(userId: Long, messageId: Long): ImMessage? {
        return roomDao.getMessage(userId, messageId)
    }

    /**
     * 根据推送的消息添加会话（会话有则未读数量加1，没有则创建）
     * @param message 推送消息
     */
    @Synchronized
    override fun addConversationForPushMessage(userId: Long, message: ImMessage) {
        val conversation = roomDao.getConversation(userId, message.conversationKey)
        // 判断消息来源是不是自己
        val isSelf = message.fromId == userId
        if (conversation == null) {
            // 如果会话不存在，创建一个会话
            roomDao.addConversation(
                ImConversation(
                    message.conversationKey,
                    message.conversationType,
                    // 如果是自己发送消息的话，未读数量为0，否则数量为1
                    if (isSelf) 0 else 1,
                    // 如果是自己发送消息的话，id为to，否则是from
                    if (message.conversationType == ImConversation.Type.SINGLE)
                        (if (isSelf) message.toId else message.fromId)
                    else
                        message.toId,
                    userId
                )
            )
        } else {
            // 要消息的来源方不是自己才加1
            if (!isSelf) {
                conversation.unReadCount += 1
                roomDao.updateConversation(conversation)
            }
        }
    }

    /**
     * 清空一个会话的未读数量
     * 自己查看了这个会话
     */
    @Synchronized
    override fun clearConversationUnReadCount(userId: Long, conversationKey: String) {
        roomDao.getConversation(userId, conversationKey)?.let {
            it.unReadCount = 0
            roomDao.updateConversation(it)
        }
    }

    /**
     * 已读一个会话的所有消息
     * 被人查看了这个会话
     */
    @Synchronized
    override fun readConversationMessage(userId: Long, conversationKey: String, readUserId: Long) {
        roomDao.deleteUnReadMessageUser(
            roomDao.getAllUnReadListOnConversation(
                userId,
                conversationKey,
                readUserId
            )
        )
    }

    /**
     * 添加用户信息，如果存在则更新(一般是主动发送消息的时候保存的)
     */
    @Synchronized
    override fun addUser(userId: Long, user: ImUser) {
        val imUser = roomDao.getUser(userId, user.id)
        user.userId = userId
        if (imUser == null) {
            roomDao.addUser(user)
        } else {
            roomDao.updateUser(user)
        }
    }

    /**
     * 获取某条消息所有的未读人员
     */
    override fun getUnReadUserId(userId: Long, messageId: Long): MutableList<UnReadMessage> {
        return ArrayList(roomDao.getUnReadList(userId, messageId))
    }

    /**
     * 添加用户未读消息
     */
    override fun addUnReadMessage(userId: Long, unReadMessageList: MutableList<UnReadMessage>) {
        unReadMessageList.forEach { it.userId = userId }
        roomDao.addUnReadMessageUser(unReadMessageList)
    }

    /**
     * 添加群信息
     */
    override fun addGroup(userId: Long, group: ImGroup) {
        group.userId = userId
        val groupInfo = roomDao.getGroupInfo(userId, group.id)
        if (groupInfo == null) {
            roomDao.addGroup(group)
        } else {
            roomDao.updateGroup(group)
        }
        roomDao.deleteUserGroup(roomDao.getUserGroupList(userId, group.id))
        roomDao.addUserGroup(group.userIdList.map {
            UserGroupEntity(userId, group.id, it)
        })
    }

    /**
     * 获取群信息
     */
    override fun getGroupInfo(userId: Long, groupId: Long): ImGroup? {
        roomDao.getGroupInfo(userId, groupId)?.let {
            it.userIdList = ArrayList(roomDao.getUserGroupList(userId, groupId)).map { m -> m.uId }
            return it
        }
        return null
    }
}