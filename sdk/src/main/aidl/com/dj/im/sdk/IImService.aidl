// IImService.aidl
package com.dj.im.sdk;

import com.dj.im.sdk.IMarsConnectListener;
import com.dj.im.sdk.IMarsListener;
// Declare any non-default types here with import statements

interface IImService {

    void connect(String token, IMarsConnectListener listener);
    void disconnect();
    void onForeground(boolean foreground);
    long getUserId();
    String getUserName();
    void setOnMarsListener(IMarsListener listener);
    void sendMessage(int cmdId, in byte[] messageData);
}
