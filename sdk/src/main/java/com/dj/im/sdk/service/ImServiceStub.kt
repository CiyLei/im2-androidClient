package com.dj.im.sdk.service

import com.dj.im.sdk.*
import com.dj.im.sdk.entity.ImUser
import com.dj.im.sdk.net.RetrofitManager
import com.dj.im.sdk.utils.RxUtil.o
import com.dj.im.sdk.utils.SpUtil
import com.tencent.mars.BaseEvent
import com.tencent.mars.stn.StnLogic
import com.umeng.message.PushAgent
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
        SpUtil.getSp(service).getString(ImService.SP_KEY_TOKEN, "")?.let {
            if (it.isNotBlank()) {
                connect(it)
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
    override fun connect(token: String) {
        compositeDisposable.dispose()
        compositeDisposable = CompositeDisposable()
        compositeDisposable.add(RetrofitManager.instance.apiStore.dns().o().subscribe({
            if (it.success) {
                service.serverList = it.data
                // 保存token
                SpUtil.getSp(service).edit().putString(DJIM.SP_KEY_TOKEN, token).apply()
                // 开启Mars服务
                service.openMars(token)
            } else {
                service.marsListener?.onConnect(it.code.toInt(), it.msg)
            }
        }, {
            service.marsListener?.onConnect(
                ResultEnum.Error_Request.code,
                ResultEnum.Error_Request.message
            )
        }))
    }

    /**
     * 退出登录
     */
    override fun disconnect() {
        // 取消注册友盟别名（应用id_用户名）
        val pushAlias = "${service.appId}_${service.userInfo?.userName}"
        PushAgent.getInstance(service)
            .deleteAlias(pushAlias, MarsCallBack.UMENG_PUSH_TYPE) { b, s ->
            }
        // 关闭Mars服务
        service.closeMars()
        service.clearToken()
        service.marsListener = null
        compositeDisposable.clear()
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
}