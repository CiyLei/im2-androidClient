// IDBDao.aidl
package com.dj.im.sdk;

import com.dj.im.sdk.entity.ImMessage;
import com.dj.im.sdk.entity.ImUser;
import com.dj.im.sdk.entity.ImConversation;

interface IDBDao {
    void addPushMessage(long userId,in ImMessage message);
    void addConversationForPushMessage(long userId,in ImMessage message);
    void addUser(long userId,in ImUser user);
    void clearConversation(long userId);
    void clearConversationUnReadCount(long userId, String conversationKey);
    void readConversationMessage(long userId, String conversationKey);
    List<ImConversation> getConversations(long userId);
    ImMessage getLastMessage(long userId, String conversationKey);
    ImMessage getMessageForId(long userId, long messageId);
    List<ImMessage> getNewestMessages(long userId, String conversationKey, int pageSize);
    ImUser getUser(long userId, long id);
    List<ImMessage> getHistoryMessage(long userId, String conversationKey, long messageId, int pageSize);
}
