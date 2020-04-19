// IMessage.aidl
package com.dj.im.sdk;

// Declare any non-default types here with import statements

interface ITask {

    int onCmdId();
    byte[] onReq2Buf();
    void onBuf2Resp(in byte[] buf);
    void onTaskEnd(in int errType, in int errCode);
}
