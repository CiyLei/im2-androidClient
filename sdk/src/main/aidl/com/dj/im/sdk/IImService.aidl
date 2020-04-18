// IImService.aidl
package com.dj.im.sdk;

import com.dj.im.sdk.IMarsListener;
import com.dj.im.sdk.ITask;
// Declare any non-default types here with import statements

interface IImService {

    void autoConnect();
    void connect(String token);
    void disconnect();
    void onForeground(boolean foreground);
    long getUserId();
    String getUserName();
    String getAlias();
    String getAvatarUrl();
    void setOnMarsListener(IMarsListener listener);
    void sendTask(ITask task);
}
