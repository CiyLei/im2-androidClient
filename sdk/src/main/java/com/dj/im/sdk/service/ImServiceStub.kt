package com.dj.im.sdk.service

import com.dj.im.sdk.*
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.net.RetrofitManager
import com.dj.im.sdk.task.HttpLogoutTask
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
    var compositeDisposable = CompositeDisposable()

    /**
     * 自动登录（检测是否有token缓存）
     */
    override fun autoConnect() {
        // 如果存在token，马上连接
        service.dbDao.getConfigValue(Constant.Key.TOKEN)?.let {
            if (it.isNotBlank()) {
                login(it)
            }
        }
    }

    /**
     * 获取用户信息
     */
    override fun getUserInfo(): ImUser? = service.userInfo

    /**
     * 登录
     *
     * @param token 登录Token
     */
    override fun login(token: String) {
        service.isLoginVerification = false
        // 保存token
        service.dbDao.putConfigValue(Constant.Key.TOKEN, token)
        compositeDisposable.dispose()
        compositeDisposable = CompositeDisposable()
        compositeDisposable.add(RetrofitManager.instance.apiStore.dns().o().subscribe({
            if (it.success) {
                service.serverList = it.data
                service.isLoginVerification = true
                // 开启Mars服务
                service.openMars(token)
            } else {
                service.marsListener?.onLogin(it.code.toInt(), it.msg)
                service.clearToken()
                service.closeMars()
            }
        }, {
            service.marsListener?.onLogin(
                ResultEnum.Error_Request.code,
                ResultEnum.Error_Request.message
            )
            service.clearToken()
            service.closeMars()
        }))
    }

    /**
     * 退出登录
     */
    override fun logout() {
        // 先访问接口，退出登录
        compositeDisposable.add(HttpLogoutTask().complete {
            // 关闭Mars服务
            service.clearToken()
            service.closeMars()
            service.marsListener = null
            compositeDisposable.dispose()
        }.start())
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

    override fun getDbDao(): IDBDao = service.dbDao

    /**
     * 发送任务
     */
    override fun sendTask(task: ITask) {
        val marsTask = StnLogic.Task(StnLogic.Task.ELong, task.onCmdId(), "", ArrayList())
        // true：无网络直接返回失败
        marsTask.networkStatusSensitive = true
        // 重试次数
        marsTask.retryCount = 0
        service.tasks[marsTask.taskID] = task
        StnLogic.startTask(marsTask)
    }

    /**
     * 设置设备唯一识别码
     */
    override fun setDeviceCode(deviceCode: String?) {
        service.deviceCode = deviceCode ?: ""
        // 重新设置设备码后重新连接
        if (service.deviceCode.isNotBlank()) {
            autoConnect()
        }
    }

    /**
     * 是否处于连接状态
     */
    override fun isConnect(): Boolean = service.isConnected
}