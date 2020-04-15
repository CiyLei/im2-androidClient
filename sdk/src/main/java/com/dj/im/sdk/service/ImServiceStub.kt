package com.dj.im.sdk.service

import com.dj.im.sdk.*
import com.dj.im.sdk.net.RetrofitManager
import com.dj.im.sdk.utils.RxUtil.o
import com.tencent.mars.BaseEvent
import com.tencent.mars.stn.StnLogic
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
                service.serverList = it.data
                // 开启Mars服务
                service.openMars(token, listener)
            } else {
                listener?.onResult(it.code.toInt(), it.msg)
            }
        }, {
            listener?.onResult(ResultEnum.Error_Request.code, ResultEnum.Error_Request.message)
        }))
    }

    /**
     * 获取用户id
     */
    override fun getUserId(): Long = service.userId

    /**
     * 获取用户名
     */
    override fun getUserName(): String = service.userName

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

    /**
     * 设置Mars的回调
     */
    override fun setOnMarsListener(listener: IMarsListener?) {
        service.marsListener = listener
    }

    override fun sendMessage(cmdId: Int, messageData: ByteArray) {
        val task = StnLogic.Task(StnLogic.Task.ELong, cmdId, "", ArrayList())
        StnLogic.startTask(task)
        service.tasks[task.taskID] = messageData
    }
}