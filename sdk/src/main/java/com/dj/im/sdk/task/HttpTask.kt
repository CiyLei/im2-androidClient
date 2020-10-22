package com.dj.im.sdk.task

import android.os.Handler
import android.os.Looper
import com.dj.im.sdk.entity.BaseResponse
import com.dj.im.sdk.utils.RxUtil.o
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Create by ChenLei on 2020/10/22
 * Describe: http接口访问的任务
 */
abstract class HttpTask<T> {

    protected val mHandler = Handler(Looper.getMainLooper())
    /**
     * 成功的回调
     */
    private var mSuccess: ((T) -> Unit)? = null

    /**
     * 失败的回调
     */
    private var mFailure: ((String?, Throwable?) -> Unit)? = null

    /**
     * 返回http任务
     */
    abstract fun httpTask(): Observable<BaseResponse<T>>

    /**
     * 开始任务
     */
    fun start(): Disposable {
        return httpTask().o().subscribe({
            if (it.success) {
                handleSuccess(it.data)
                mSuccess?.invoke(it.data)
            } else {
                handleFailure(it.msg, null)
                mFailure?.invoke(it.msg, null)
            }
        }, {
            mFailure?.invoke(null, it)
        })
    }

    /**
     * 内部处理成功
     */
    open fun handleSuccess(data: T) {

    }

    /**
     * 内部处理失败
     */
    open fun handleFailure(failureMsg: String?, failureError: Throwable?) {

    }

    /**
     * 是在成功事件
     */
    fun success(event: ((T) -> Unit)): HttpTask<T> {
        mSuccess = event
        return this
    }

    /**
     * 是在失败事件
     */
    fun failure(event: ((String?, Throwable?) -> Unit)): HttpTask<T> {
        mFailure = event
        return this
    }
}