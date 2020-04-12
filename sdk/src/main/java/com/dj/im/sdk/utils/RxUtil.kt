package com.dj.im.sdk.utils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Create by ChenLei on 2020/4/11
 * Describe: RxJava 工具类
 */
internal object RxUtil {

    /**
     * 快捷的选择执行线程
     */
    fun <T> Observable<T>.o(): Observable<T> {
        return subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
