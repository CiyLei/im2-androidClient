// IMarsListener.aidl
package com.dj.im.sdk;

// Declare any non-default types here with import statements

interface IMarsListener {

    void onPush(int cmdId, in byte[] data);
}
