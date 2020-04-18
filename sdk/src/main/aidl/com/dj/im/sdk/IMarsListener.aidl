// IMarsListener.aidl
package com.dj.im.sdk;

// Declare any non-default types here with import statements

interface IMarsListener {

    void onConnect(int code, String message);
    void onPushMessage(long messageId);
    void onChangeConversions();
    void onChangeMessageState(long messageId, int state);
}
