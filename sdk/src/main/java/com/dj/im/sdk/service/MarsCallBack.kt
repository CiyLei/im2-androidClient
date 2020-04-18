package com.dj.im.sdk.service

import android.util.Log
import com.dj.im.sdk.Constant
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.entity.User
import com.dj.im.sdk.message.AuthMessage
import com.dj.im.sdk.message.PushMessage
import com.dj.im.sdk.message.ResponseMessage
import com.dj.im.sdk.utils.EncryptUtil
import com.dj.im.sdk.utils.HexUtil
import com.dj.im.sdk.utils.SpUtil
import com.dj.im.server.modules.im.message.PushConversation
import com.tencent.mars.app.AppLogic
import com.tencent.mars.sdt.SdtLogic
import com.tencent.mars.stn.StnLogic
import java.io.ByteArrayOutputStream
import java.util.*


/**
 * Create by ChenLei on 2020/4/11
 * Describe: Mars各种回调
 */
internal class MarsCallBack(private val service: ImService, val token: String) : SdtLogic.ICallBack,
    StnLogic.ICallBack,
    AppLogic.ICallBack {

    companion object {
        // 设备名称
        private val DEVICE_NAME = android.os.Build.MANUFACTURER + "-" + android.os.Build.MODEL
        // 设备型号
        private var DEVICE_TYPE = "android-" + android.os.Build.VERSION.SDK_INT
        private val info = AppLogic.DeviceInfo(
            DEVICE_NAME,
            DEVICE_TYPE
        )
    }

    // 通话秘钥
    private var mCipherKey: ByteArray? = null

    /**
     * 信令探测回调接口，启动信令探测
     */
    override fun reportSignalDetectResults(resultsJson: String?) {
    }

    /**
     * 收到SVR PUSH下来的消息
     * @param cmdid
     * @param data
     */
    override fun onPush(cmdid: Int, data: ByteArray?) {
        if (service.userInfo == null) {
            return
        }
        // 解密
        val responseData = EncryptUtil.symmetricDecrypt(mCipherKey, data)
        if (responseData != null) {
            Log.d(
                "MarsCallBack",
                "【推送解密成功,cmdid:$cmdid,秘钥:${Arrays.toString(mCipherKey)},解密前:${Arrays.toString(data)},解密后:${Arrays.toString(
                    responseData
                )}】"
            )
            val response = ResponseMessage.Response.parseFrom(responseData)
            when (cmdid) {
                Constant.CMD.PUSH_MESSAGE -> {
                    // 有推送消息
                    val pushResponse = PushMessage.PushMessageResponse.parseFrom(response.data)
                    // 保存消息
                    service.conversationDao.addPushMessage(service.userInfo!!.id, pushResponse)
                    // 保存消息对方的用户消息
                    service.conversationDao.addUser(
                        service.userInfo!!.id,
                        pushResponse.otherSideUserInfo
                    )
                    // 添加会话
                    service.conversationDao.addConversationForPushMessage(
                        service.userInfo!!.id,
                        pushResponse
                    )
                    service.marsListener?.onPushMessage(pushResponse.id)
                    service.marsListener?.onChangeConversions()
                    service.marsListener?.onChangeMessageState(
                        pushResponse.id,
                        Constant.MessageSendState.SUCCESS
                    )
                }
                Constant.CMD.PUSH_CONVERSATION -> {
                    val conversationResponse =
                        PushConversation.PushConversationResponse.parseFrom(response.data)
                    // 先清空会话信息
                    service.conversationDao.clearConversation(service.userInfo!!.id)
                    // 保存到数据库中
                    for (conversation in conversationResponse.conversationsList) {
                        service.conversationDao.addUser(
                            service.userInfo!!.id,
                            conversation.toUserInfo
                        )
                        service.conversationDao.addConversation(service.userInfo!!.id, conversation)
                    }
                    // 通知回调
                    service.marsListener?.onChangeConversions()
                    Log.d("MarsCallBack", conversationResponse.toString())
                }
            }
        } else {
            Log.d("MarsCallBack", "【推送解密失败,cmdid:$cmdid,data:${Arrays.toString(data)}】")
        }
    }

    /**
     * SDK要求上层做域名解析.上层可以实现传统DNS解析,或者自己实现的域名/IP映射
     * @param host
     * @return
     */
    override fun onNewDns(host: String?): Array<String>? {
        if (host == ImService.HOST) {
            // 返回推荐服务器地址
            return arrayOf(service.serverList!!.recommend)
        }
        return null
    }

    /**
     * 连接状态通知
     * @param status    综合状态，即长连+短连的状态
     * @param longlinkstatus    仅长连的状态
     */
    override fun reportConnectInfo(status: Int, longlinkstatus: Int) {
    }

    /**
     * 任务结束回调
     * @param taskID            任务标识
     * @param userContext
     * @param errType           错误类型
     * @param errCode           错误码
     * @return
     */
    override fun onTaskEnd(taskID: Int, userContext: Any?, errType: Int, errCode: Int): Int {
        service.tasks.remove(taskID)?.onTaskEnd(errType, errCode)
        return 0
    }

    /**
     * 流量统计
     * @param send
     * @param recv
     */
    override fun trafficData(send: Int, recv: Int) {
    }

    override fun reportTaskProfile(taskString: String?) {
    }

    /**
     * SDK要求上层做认证操作(可能新发起一个AUTH CGI)
     * @return
     */
    override fun makesureAuthed(host: String?): Boolean = true

    /**
     * 请求做sync
     */
    override fun requestDoSync() {
    }

    /**
     * SDK要求上层对TASK解包
     * @param taskID        任务标识
     * @param userContext
     * @param respBuffer    要解包的BUFFER
     * @param errCode       解包的错误码
     * @return  int
     */
    override fun buf2Resp(
        taskID: Int,
        userContext: Any?,
        respBuffer: ByteArray?,
        errCode: IntArray?,
        channelSelect: Int
    ): Int {
        if (!service.tasks.containsKey(taskID)) {
            return StnLogic.RESP_FAIL_HANDLE_TASK_END
        }
        try {
            // 解密
            val responseData = EncryptUtil.symmetricDecrypt(mCipherKey, respBuffer)
            service.tasks[taskID]?.onBuf2Resp(responseData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return StnLogic.RESP_FAIL_HANDLE_TASK_END
    }

    /**
     * SDK要求上层生成长链接数据校验包,在长链接连接上之后使用,用于验证SVR身份
     * @param identifyReqBuf    校验包数据内容
     * @param hashCodeBuffer    校验包的HASH
     * @param reqRespCmdID      数据校验的CMD ID
     * @return  ECHECK_NOW(需要校验), ECHECK_NEVER(不校验), ECHECK_NEXT(下一次再询问)
     */
    override fun getLongLinkIdentifyCheckBuffer(
        identifyReqBuf: ByteArrayOutputStream?,
        hashCodeBuffer: ByteArrayOutputStream?,
        reqRespCmdID: IntArray?
    ): Int {
        // 生成对称秘钥
        mCipherKey = EncryptUtil.generateSymmetricEncryptionKey()
        // 对称加密后的密文
        val asymmetricalEncrypt = EncryptUtil.asymmetricalEncrypt(mCipherKey)
        // 发送验证
        val request = AuthMessage.AuthRequest.newBuilder().setAppId(service.appId)
            .setAppMobileSecret(service.appSecret).setToken(token).setDevice(0)
            .setDeviceCode(service.deviceCode)
            .setCipherKey(HexUtil.hex2String(asymmetricalEncrypt))
            .build()
        identifyReqBuf?.write(request.toByteArray())
        reqRespCmdID?.set(0, Constant.CMD.AUTH)
        Log.d("MarsCallBack", "【连接交换秘钥：${Arrays.toString(mCipherKey)}】")
        return StnLogic.ECHECK_NOW
    }

    /**
     * SDK要求上层解连接校验回包.
     * @param buffer            SVR回复的连接校验包
     * @param hashCodeBuffer    CLIENT请求的连接校验包的HASH值
     * @return
     */
    override fun onLongLinkIdentifyResp(buffer: ByteArray?, hashCodeBuffer: ByteArray?): Boolean {
        val response = ResponseMessage.Response.parseFrom(buffer)
        if (response.success) {
            val authResponse = AuthMessage.AuthResponse.parseFrom(response.data)
            val userResponse = authResponse.userInfo
            service.userInfo =
                User(
                    userResponse.userId,
                    userResponse.userName,
                    userResponse.alias,
                    userResponse.avatarUrl
                )
            // 保存自己的用户消息
            service.conversationDao.addUser(userResponse.userId, service.userInfo!!)
            // 回调连接
            service.marsListener?.onConnect(ResultEnum.Success.code, ResultEnum.Success.message)
            // 保存token
            SpUtil.getSp(service).edit().putString(ImService.SP_KEY_TOKEN, token).apply()
        } else {
            service.marsListener?.onConnect(response.code, response.msg)
            service.clearToken()
        }
        return response.success
    }

    /**
     * SDK要求上层对TASK组包
     * @param taskID    任务标识
     * @param userContext
     * @param reqBuffer 组包的BUFFER
     * @param errCode   组包的错误码
     * @return
     */
    override fun req2Buf(
        taskID: Int,
        userContext: Any?,
        reqBuffer: ByteArrayOutputStream?,
        errCode: IntArray?,
        channelSelect: Int,
        host: String?
    ): Boolean {
        val data = service.tasks[taskID]
        if (data != null) {
            try {
                // 加密，发送消息
                val symmetricEncrypt = EncryptUtil.symmetricEncrypt(mCipherKey, data.onReq2Buf())
                reqBuffer?.write(symmetricEncrypt)
                Log.d(
                    "MarsCallBack",
                    "【发送消息成功,秘钥:${Arrays.toString(mCipherKey)},加密后的内容:${Arrays.toString(
                        symmetricEncrypt
                    )}】"
                )
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
        return false
    }

    /**
     * 是否登录
     * @return true 登录 false 未登录
     */
    override fun isLogoned(): Boolean {
        return false
    }

    override fun requestNetCheckShortLinkHosts(): Array<String> = emptyArray()

    /**
     * STN 会将配置文件进行存储，如连网IPPort策略、心跳策略等，此类信息将会被存储在客户端上层指定的目录下
     * @return APP目录
     */
    override fun getAppFilePath(): String = service.cacheDir.path

    /**
     * STN 会根据客户端的登陆状态进行网络连接策略的动态调整，当用户非登陆态时，网络会将连接的频率降低
     * 所以需要获取用户的帐号信息，判断用户是否已登录
     * @return 用户帐号信息
     */
    override fun getAccountInfo(): AppLogic.AccountInfo = AppLogic.AccountInfo(
        Random(System.currentTimeMillis() / 1000).nextInt().toLong(), "djIM"
    )

    /**
     * 客户端版本号能够帮助 STN 清晰区分存储的网络策略配置文件。
     * @return 客户端版本号
     */
    override fun getClientVersion(): Int = ImService.CLIENT_VERSION

    /**
     * 客户端通过获取设备类型，加入到不同的上报统计回调中，供客户端进行数据分析
     * @return
     */
    override fun getDeviceType(): AppLogic.DeviceInfo =
        info

}
