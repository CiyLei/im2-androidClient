package com.dj.im.sdk.utils

import com.dj.im.sdk.Constant

/**
 * Create by ChenLei on 2020/12/28
 * Describe: http工具类
 */
internal object HttpUtil {
    /**
     * 将文件url转为转为完整的http url
     */
    fun toFileUrl(url: String): String {
        return when {
            url.isBlank() -> ""
            url.startsWith("http") -> url
            else -> Constant.URL.FILE_URL + url
        }
    }
}