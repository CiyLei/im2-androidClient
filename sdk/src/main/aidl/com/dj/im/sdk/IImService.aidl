// IImService.aidl
package com.dj.im.sdk;

import com.dj.im.sdk.IMarsListener;
import com.dj.im.sdk.ITask;
import com.dj.im.sdk.IDBDao;
import com.dj.im.sdk.entity.ImUser;
// Declare any non-default types here with import statements

interface IImService {

    void autoConnect();
    void login(String token);
    void logout();
    void onForeground(boolean foreground);
    ImUser getUserInfo();
    IDBDao getDbDao();
    void setOnMarsListener(IMarsListener listener);
    void sendTask(ITask task);
}
