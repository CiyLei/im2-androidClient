package com.dj.im.sdk.db

import android.content.Context
import com.dj.im.sdk.IDBDao
import com.dj.im.sdk.entity.*
import com.dj.im.sdk.proto.PrPushConversation
import com.dj.im.sdk.utils.MessageConvertUtil

/**
 * Create by ChenLei on 2020/4/20
 * Describe: im数据库Dao层
 */
internal class ImDbDao(context: Context) : IDBDao.Stub() {

    private val mImUserDao = ImRoomDatabase.instance(context).imUserDao()
    private val mImGroupDao = ImRoomDatabase.instance(context).imGroupDao()
    private val mImConversationDao = ImRoomDatabase.instance(context).imConversationDao()
    private val mImMessageDao = ImRoomDatabase.instance(context).imMessageDao()
    private val mImUnReadRoomDao = ImRoomDatabase.instance(context).imUnReadRoomDao()
    private val mImUserGroupRoomDao = ImRoomDatabase.instance(context).imUserGroupRoomDao()
    private val mConfigRoomDao = ImRoomDatabase.instance(context).imConfigRoomDao()

    /**
     * 获取指定消息之前的消息列表（即读取历史消息）
     * @param pageSize 如果等于-1就查询全部
     */
    @Synchronized
    override fun getHistoryMessage(
        belongAppId: String,
        belongUserName: String,
        conversationKey: String,
        messageId: Long,
        pageSize: Int
    ): MutableList<ImMessage> {
        if (pageSize > 0) {
            return mImMessageDao.getPreviousMessageList(
                belongAppId,
                belongUserName,
                conversationKey,
                messageId,
                pageSize
            )
        }
        return mImMessageDao.getPreviousAllMessageList(
            belongAppId,
            belongUserName,
            conversationKey,
            messageId
        )
    }

    /**
     * 添加消息到数据库
     */
    @Synchronized
    override fun addPushMessage(belongAppId: String, belongUserName: String, message: ImMessage) {
        val imMessage = mImMessageDao.getMessage(belongAppId, belongUserName, message.id)
        message.belongAppId = belongAppId
        message.belongUserName = belongUserName
        if (imMessage == null) {
            mImMessageDao.insert(message)
        } else {
            mImMessageDao.update(message)
        }
    }

    /**
     * 清除会话缓存
     */
    @Synchronized
    override fun clearConversation(belongAppId: String, belongUserName: String) {
        val conversationList = mImConversationDao.getConversationList(belongAppId, belongUserName)
        mImConversationDao.deleteList(conversationList)
    }

    /**
     * 获取某个用户的会话信息
     */
    @Synchronized
    override fun getConversations(
        belongAppId: String,
        belongUserName: String
    ): MutableList<ImConversation> {
        return mImConversationDao.getConversationList(belongAppId, belongUserName)
    }

    /**
     * 获取用户信息
     * @param userId 当前用户
     * @param id 查询的用户id
     */
    @Synchronized
    override fun getUser(belongAppId: String, belongUserName: String, userName: String): ImUser? {
        return mImUserDao.getUser(belongAppId, belongUserName, userName)
    }

    /**
     * 获取某个会话的最后一条消息
     */
    @Synchronized
    override fun getLastMessage(
        belongAppId: String,
        belongUserName: String,
        conversationKey: String
    ): ImMessage? {
        return mImMessageDao.getConversationLastMessage(
            belongAppId,
            belongUserName,
            conversationKey
        )
    }

    /**
     * 添加会话信息，如果存在则更新
     */
    @Synchronized
    fun addConversation(
        belongAppId: String,
        belongUserName: String,
        conversation: PrPushConversation.Conversation
    ) {
        mImConversationDao.insert(
            ImConversation(
                belongAppId,
                belongUserName,
                conversation.conversationKey,
                conversation.conversationType,
                conversation.unReadCount,
                if (conversation.conversationType == ImConversation.Type.SINGLE)
                    conversation.otherSideUserInfo.userName
                else
                    conversation.groupInfo.groupId.toString()
            )
        )
        conversation.messagesList.forEach {
            addPushMessage(
                belongAppId,
                belongUserName,
                MessageConvertUtil.prPushMessage2ImMessage(belongAppId, belongUserName, it)
            )
            mImUnReadRoomDao.deleteList(
                mImUnReadRoomDao.getAllUnReadListForMessageId(
                    belongAppId,
                    belongUserName,
                    it.id
                )
            )
            // 保存未读信息
            val unReadUserIdList = it.unReadUserNameListList
            addUnReadMessage(belongAppId, belongUserName, ArrayList(unReadUserIdList.map { m ->
                UnReadMessage(belongAppId, belongUserName, it.id, m)
            }))
        }
    }

    /**
     * 获取最新的消息列表（最新的在前面）
     */
    @Synchronized
    override fun getNewestMessages(
        belongAppId: String,
        belongUserName: String,
        conversationKey: String,
        pageSize: Int
    ): MutableList<ImMessage> {
        return mImMessageDao.getNewMessage(belongAppId, belongUserName, conversationKey, pageSize)
    }

    /**
     * 根据消息id获取消息
     */
    @Synchronized
    override fun getMessageForId(
        belongAppId: String,
        belongUserName: String,
        messageId: Long
    ): ImMessage? {
        return mImMessageDao.getMessage(belongAppId, belongUserName, messageId)
    }

    /**
     * 根据推送的消息添加会话（会话有则未读数量加1，没有则创建）
     * @param message 推送消息
     */
    @Synchronized
    override fun addConversationForPushMessage(
        belongAppId: String,
        belongUserName: String,
        message: ImMessage
    ) {
        val conversation =
            mImConversationDao.getConversation(belongAppId, belongUserName, message.conversationKey)
        // 判断消息来源是不是自己
        val isSelf = message.fromUserName == belongUserName
        if (conversation == null) {
            // 如果会话不存在，创建一个会话
            mImConversationDao.insert(
                ImConversation(
                    belongAppId,
                    belongUserName,
                    message.conversationKey,
                    message.conversationType,
                    // 如果是自己发送消息的话，未读数量为0，否则数量为1
                    if (isSelf) 0 else 1,
                    // 如果是自己发送消息的话，id为to，否则是from
                    if (message.conversationType == ImConversation.Type.SINGLE)
                        (if (isSelf) message.toUserName else message.fromUserName)
                    else
                        message.toUserName
                )
            )
        } else {
            // 要消息的来源方不是自己才加1
            if (!isSelf) {
                conversation.unReadCount += 1
                mImConversationDao.update(conversation)
            }
        }
    }

    /**
     * 清空一个会话的未读数量
     * 自己查看了这个会话
     */
    @Synchronized
    override fun clearConversationUnReadCount(
        belongAppId: String,
        belongUserName: String,
        conversationKey: String
    ) {
        mImConversationDao.getConversation(belongAppId, belongUserName, conversationKey)?.let {
            it.unReadCount = 0
            mImConversationDao.update(it)
        }
    }

    /**
     * 已读一个会话的所有消息
     * 被人查看了这个会话
     */
    @Synchronized
    override fun readConversationMessage(
        belongAppId: String,
        belongUserName: String,
        conversationKey: String,
        readUserName: String
    ) {
        mImUnReadRoomDao.deleteList(
            mImUnReadRoomDao.getAllUnReadListOnConversation(
                belongAppId,
                belongUserName,
                conversationKey,
                readUserName
            )
        )
    }

    /**
     * 添加用户信息，如果存在则更新(一般是主动发送消息的时候保存的)
     */
    @Synchronized
    override fun addUser(belongAppId: String, belongUserName: String, user: ImUser) {
        val imUser = mImUserDao.getUser(belongAppId, belongUserName, user.userName)
        user.belongAppId = belongAppId
        user.belongUserName = belongUserName
        if (imUser == null) {
            mImUserDao.insert(user)
        } else {
            mImUserDao.update(user)
        }
    }

    /**
     * 获取某条消息所有的未读人员
     */
    override fun getUnReadUserId(
        belongAppId: String,
        belongUserName: String,
        messageId: Long
    ): MutableList<UnReadMessage> {
        return ArrayList(mImUnReadRoomDao.getUnReadList(belongAppId, belongUserName, messageId))
    }

    /**
     * 添加用户未读消息
     */
    override fun addUnReadMessage(
        belongAppId: String,
        belongUserName: String,
        unReadMessageList: MutableList<UnReadMessage>
    ) {
        unReadMessageList.forEach {
            it.belongAppId = belongAppId
            it.belongUserName = belongUserName
        }
        mImUnReadRoomDao.insertList(unReadMessageList)
    }

    /**
     * 添加群信息
     */
    override fun addGroup(belongAppId: String, belongUserName: String, group: ImGroup) {
        group.belongAppId = belongAppId
        group.belongUserName = belongUserName
        val groupInfo = mImGroupDao.getGroupInfo(belongAppId, belongUserName, group.id)
        if (groupInfo == null) {
            mImGroupDao.insert(group)
        } else {
            mImGroupDao.update(group)
        }
        mImUserGroupRoomDao.deleteList(
            mImUserGroupRoomDao.getUserGroupList(
                belongAppId,
                belongUserName,
                group.id
            )
        )
        mImUserGroupRoomDao.insertList(group.userNameList.map {
            UserGroupEntity(belongAppId, belongUserName, group.id, it)
        })
    }

    /**
     * 获取群信息
     */
    override fun getGroupInfo(
        belongAppId: String,
        belongUserName: String,
        groupId: Long
    ): ImGroup? {
        mImGroupDao.getGroupInfo(belongAppId, belongUserName, groupId)?.let {
            it.userNameList = ArrayList(
                mImUserGroupRoomDao.getUserGroupList(
                    belongAppId,
                    belongUserName,
                    groupId
                )
            ).map { m -> m.userName }
            return it
        }
        return null
    }

    /**
     * 获取配置的值
     */
    override fun getConfigValue(key: String?): String? {
        return mConfigRoomDao.getValue(key ?: return null)?.value
    }

    /**
     * 添加配置
     */
    override fun putConfigValue(key: String?, value: String?) {
        key ?: return
        value ?: return
        val config = mConfigRoomDao.getValue(key)
        if (config == null) {
            mConfigRoomDao.insert(ConfigEntity(key, value))
        } else {
            mConfigRoomDao.update(ConfigEntity(key, value))
        }
    }

    /**
     * 删除配置
     */
    override fun deleteConfig(key: String?) {
        mConfigRoomDao.delete(ConfigEntity(key ?: return, ""))
    }
}