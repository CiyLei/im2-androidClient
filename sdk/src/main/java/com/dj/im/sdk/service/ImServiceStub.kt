package com.dj.im.sdk.service

import com.dj.im.sdk.IImService
import com.dj.im.sdk.IMarsConnectListener
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.net.RetrofitManager
import com.dj.im.sdk.utils.RxUtil.o
import com.tencent.mars.BaseEvent
import io.reactivex.disposables.CompositeDisposable


/**
 * Create by ChenLei on 2020/4/11
 * Describe: Im服务的Binder
 */
internal class ImServiceStub(private val service: ImService) : IImService.Stub() {

    // 管理订阅
    private val mCompositeDisposable = CompositeDisposable()

    /**
     * 登录
     *
     * @param token 登录Token
     */
    override fun connect(token: String, listener: IMarsConnectListener?) {
        mCompositeDisposable.add(RetrofitManager.instance.apiStore.dns().o().subscribe({
            if (it.success) {
                ImService.SERVER_SITUATION = it.data
                // 开启Mars服务
                service.openMars(token, listener)
            } else {
                listener?.result(it.code.toInt(), it.msg)
            }
        }, {
            listener?.result(ResultEnum.Error_Request.code, ResultEnum.Error_Request.message)
        }))
    }

    /**
     * 退出登录
     */
    override fun disconnect() {
        // 关闭Mars服务
        service.closeMars()
        mCompositeDisposable.clear()
    }

    /**
     * 设置是否在前台
     */
    override fun onForeground(foreground: Boolean) {
        BaseEvent.onForeground(foreground)
    }
}