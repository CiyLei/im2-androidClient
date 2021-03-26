// IMarsListener.aidl
package com.dj.im.sdk;

// Declare any non-default types here with import statements

interface IMarsListener {

    void onLogin(int code, String message);
    void onPushMessage(long messageId);
    void onChangeConversions();
    void onChangeMessageState(String conversationKey, long messageId, int state);
    void onChangeConversationRead(String conversationKey,String readUserName);
    void onOffline(int code, String message);
    void onConnect(boolean isConnect);
    void onRevokeMessage(String conversationKey, long messageId);
}
