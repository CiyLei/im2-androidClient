// IConnectListener.aidl
package com.dj.im.sdk;

// Declare any non-default types here with import statements

// 连接回调
interface IMarsConnectListener {
    void result(int resultCode, String resultMessage);
}
