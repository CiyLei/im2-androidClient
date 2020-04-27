// IDBDao.aidl
package com.dj.im.sdk;

import com.dj.im.sdk.entity.ImMessage;
import com.dj.im.sdk.entity.ImUser;
import com.dj.im.sdk.entity.ImGroup;
import com.dj.im.sdk.entity.ImConversation;
import com.dj.im.sdk.entity.UnReadMessage;

interface IDBDao {
    void addPushMessage(long userId,in ImMessage message);
    void addConversationForPushMessage(long userId,in ImMessage message);
    void addUser(long userId,in ImUser user);
    void clearConversation(long userId);
    void clearConversationUnReadCount(long userId, String conversationKey);
    void readConversationMessage(long userId, String conversationKey, long readUserId);
    List<ImConversation> getConversations(long userId);
    ImMessage getLastMessage(long userId, String conversationKey);
    ImMessage getMessageForId(long userId, long messageId);
    List<ImMessage> getNewestMessages(long userId, String conversationKey, int pageSize);
    ImUser getUser(long userId, long id);
    List<ImMessage> getHistoryMessage(long userId, String conversationKey, long messageId, int pageSize);
    List<UnReadMessage> getUnReadUserId(long userId, long messageId);
    void addUnReadMessage(long userId,in List<UnReadMessage> unReadMessageList);
    void addGroup(long userId, in ImGroup group);
    ImGroup getGroupInfo(long userId, long groupId);
}
