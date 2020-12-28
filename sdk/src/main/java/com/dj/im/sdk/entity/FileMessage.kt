package com.dj.im.sdk.entity

import com.dj.im.sdk.convert.message.Message
import com.dj.im.sdk.service.ServiceManager
import com.dj.im.sdk.utils.HttpUtil
import com.google.gson.Gson
import java.io.File
import java.io.Serializable

/**
 * Create by ChenLei on 2020/4/21
 * Describe: 文件消息
 */
open class FileMessage : Message {

    private val mGson = Gson()

    // 保存发送本地路径和网络路径
    var fileEntity: FileEntity

    // 上传进度
    var uploadProgress = 0.0f

    constructor(file: File) : super(
        ImMessage(
            ServiceManager.instance.mAppKey,
            ServiceManager.instance.getUserInfo()?.userName ?: "",
            data = Gson().toJson(FileEntity(file.absolutePath, file.name)),
            type = ImMessage.Type.FILE,
            summary = "[文件]"
        )
    ) {
        fileEntity = FileEntity(file.absolutePath, file.name)
    }

    constructor(imMessage: ImMessage) : super(imMessage) {
        fileEntity = try {
            mGson.fromJson(imMessage.data, FileEntity::class.java)
        } catch (e: Throwable) {
            e.printStackTrace()
            FileEntity()
        }
    }

    /**
     * 更新数据
     */
    fun updateData() {
        imMessage.data = mGson.toJson(fileEntity)
    }

    /**
     * 保存到数据库前先更新一遍data
     */
    override fun save() {
        updateData()
        super.save()
    }

    /**
     * 获取远程url
     */
    fun getFileHttpUrl(): String = HttpUtil.toFileUrl(fileEntity.url)

}

data class FileEntity(
    /**
     * 本地文件路径
     */
    var localPath: String? = null,
    /**
     * 文件名称
     */
    var fileName: String = "",
    /**
     * 网络资源id
     */
    var url: String = "",
    /**
     * 额外的信息
     */
    var extra: HashMap<String, String> = HashMap()
) : Serializable