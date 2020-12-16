package com.dj.im.sdk.service

import android.util.Log
import com.dj.im.sdk.Constant
import com.dj.im.sdk.ResultEnum
import com.dj.im.sdk.proto.PrAuth
import com.dj.im.sdk.proto.PrResponseMessage
import com.dj.im.sdk.utils.EncryptUtil
import com.dj.im.sdk.utils.HexUtil
import com.dj.im.sdk.utils.MessageConvertUtil
import com.dj.im.sdk.utils.SpUtil
import com.tencent.mars.app.AppLogic
import com.tencent.mars.sdt.SdtLogic
import com.tencent.mars.stn.StnLogic
import java.io.ByteArrayOutputStream
import java.util.*


/**
 * Create by ChenLei on 2020/4/11
 * Describe: Mars各种回调
 */
internal class MarsCallBack(private val mService: ImService, private val mToken: String) :
    SdtLogic.ICallBack,
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

        // 友盟推送类型
        val UMENG_PUSH_TYPE = "IM"
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
        if (mService.userInfo == null) {
            return
        }
        // 解密
        val responseData = EncryptUtil.symmetricDecrypt(mCipherKey, data)
        if (responseData != null) {
            Log.d(
                "MarsCallBack",
                "【推送解密成功,cmdid:$cmdid,秘钥:${Arrays.toString(mCipherKey)},解密前:${Arrays.toString(data)},解密后:${
                    Arrays.toString(
                        responseData
                    )
                }】"
            )
            val response = PrResponseMessage.Response.parseFrom(responseData)
            mService.pushHandler[cmdid]?.onHandle(response)
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
            return arrayOf(mService.serverList!!.recommend)
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
        mService.tasks.remove(taskID)?.onTaskEnd(errType, errCode)
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
        if (!mService.tasks.containsKey(taskID)) {
            return StnLogic.RESP_FAIL_HANDLE_TASK_END
        }
        try {
            // 解密
            val responseData = EncryptUtil.symmetricDecrypt(mCipherKey, respBuffer)
            mService.tasks[taskID]?.onBuf2Resp(responseData)
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
        // 加密app秘钥
        val appMobileSecret =
            EncryptUtil.symmetricEncrypt(mCipherKey, mService.appSecret.toByteArray())
        // 加密token
        val token = EncryptUtil.symmetricEncrypt(mCipherKey, mToken.toByteArray())
        // 发送验证
        val request = PrAuth.AuthRequest.newBuilder().setAppId(mService.appId)
            .setAppMobileSecret(HexUtil.hex2String(appMobileSecret))
            .setToken(HexUtil.hex2String(token)).setDevice(0)
            .setDeviceCode(mService.deviceCode)
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
        val response = PrResponseMessage.Response.parseFrom(buffer)
        if (response.success) {
            val authResponse = PrAuth.AuthResponse.parseFrom(response.data)
            val userResponse = authResponse.userInfo
            mService.userInfo = MessageConvertUtil.prUser2ImUser(
                mService.appId,
                userResponse.userName,
                userResponse
            )
            // 保存自己的用户消息
            mService.dbDao.addUser(mService.appId, userResponse.userName, mService.userInfo!!)
            // 回调连接
            mService.marsListener?.onConnect(ResultEnum.Success.code, ResultEnum.Success.message)
            // 保存token
            SpUtil.getSp(mService).edit().putString(ImService.SP_KEY_TOKEN, mToken).apply()
        } else {
            mService.marsListener?.onConnect(response.code, response.msg)
            mService.clearToken()
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
        val data = mService.tasks[taskID]
        if (data != null) {
            try {
                // 加密，发送消息
                val symmetricEncrypt = EncryptUtil.symmetricEncrypt(mCipherKey, data.onReq2Buf())
                reqBuffer?.write(symmetricEncrypt)
                Log.d(
                    "MarsCallBack",
                    "【发送消息成功,秘钥:${Arrays.toString(mCipherKey)},加密后的内容:${
                        Arrays.toString(
                            symmetricEncrypt
                        )
                    }】"
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
        return mService.userInfo == null
    }

    override fun requestNetCheckShortLinkHosts(): Array<String> = emptyArray()

    /**
     * STN 会将配置文件进行存储，如连网IPPort策略、心跳策略等，此类信息将会被存储在客户端上层指定的目录下
     * @return APP目录
     */
    override fun getAppFilePath(): String = mService.cacheDir.path

    /**
     * STN 会根据客户端的登陆状态进行网络连接策略的动态调整，当用户非登陆态时，网络会将连接的频率降低
     * 所以需要获取用户的帐号信息，判断用户是否已登录
     * @return 用户帐号信息
     */
    override fun getAccountInfo(): AppLogic.AccountInfo = AppLogic.AccountInfo(
        mService.userInfo?.id ?: 0L, mService.userInfo?.userName ?: ""
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
