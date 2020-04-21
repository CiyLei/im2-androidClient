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
//    void addConversation(long userId,in ImConversation conversation);
    void clearConversationUnReadCount(long userId, String conversationId);
    void readConversationMessage(long userId, String conversationId);
    List<ImConversation> getConversations(long userId);
    ImMessage getLastMessage(long userId, String conversationId);
    ImMessage getMessageForId(long userId, long messageId);
    List<ImMessage> getNewestMessages(long userId, String conversationId, int pageSize);
    ImUser getUser(long userId, long id);
    List<ImMessage> getHistoryMessage(long userId, String conversationId, long messageId, int pageSize);
}
