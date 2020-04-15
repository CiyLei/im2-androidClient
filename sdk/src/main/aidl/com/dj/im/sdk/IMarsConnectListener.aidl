// IConnectListener.aidl
package com.dj.im.sdk;

// Declare any non-default types here with import statements

// 连接回调
interface IMarsConnectListener {
    void onResult(int resultCode, String resultMessage);
}
