// IImService.aidl
package com.dj.im.sdk;

import com.dj.im.sdk.IMarsConnectListener;
// Declare any non-default types here with import statements

interface IImService {

    void connect(String token, IMarsConnectListener listener);
    void disconnect();
    void onForeground(boolean foreground);
}
