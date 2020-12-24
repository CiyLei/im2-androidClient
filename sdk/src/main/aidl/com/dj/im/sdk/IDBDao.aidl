// IDBDao.aidl
package com.dj.im.sdk;

import com.dj.im.sdk.entity.ImMessage;
import com.dj.im.sdk.entity.ImUser;
import com.dj.im.sdk.entity.ImGroup;
import com.dj.im.sdk.entity.ImConversation;
import com.dj.im.sdk.entity.UnReadMessage;

interface IDBDao {
    void addPushMessage(String belongAppId, String belongUserName,in ImMessage message);
    void addConversationForPushMessage(String belongAppId, String belongUserName,in ImMessage message);
    void addUser(String belongAppId, String belongUserName,in ImUser user);
    void clearConversation(String belongAppId, String belongUserName);
    void clearConversationUnReadCount(String belongAppId, String belongUserName, String conversationKey);
    void readConversationMessage(String belongAppId, String belongUserName, String conversationKey, String readUserName);
    List<ImConversation> getConversations(String belongAppId, String belongUserName);
    ImMessage getLastMessage(String belongAppId, String belongUserName, String conversationKey);
    ImMessage getMessageForId(String belongAppId, String belongUserName, long messageId);
    List<ImMessage> getNewestMessages(String belongAppId, String belongUserName, String conversationKey, int pageSize);
    ImUser getUser(String belongAppId, String belongUserName, String userName);
    List<ImMessage> getHistoryMessage(String belongAppId, String belongUserName, String conversationKey, long messageId, int pageSize);
    List<UnReadMessage> getUnReadUserId(String belongAppId, String belongUserName, long messageId);
    void addUnReadMessage(String belongAppId, String belongUserName,in List<UnReadMessage> unReadMessageList);
    void addGroup(String belongAppId, String belongUserName, in ImGroup group);
    ImGroup getGroupInfo(String belongAppId, String belongUserName, long groupId);
    String getConfigValue(String key);
    void putConfigValue(String key, String value);
    void deleteConfig(String key);
}
