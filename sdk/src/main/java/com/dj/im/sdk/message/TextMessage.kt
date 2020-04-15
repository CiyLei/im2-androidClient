package com.dj.im.sdk.message

/**
 * Create by ChenLei on 2020/4/15
 * Describe: 文字消息实体类
 */
open class TextMessage(data: String) : Message() {

    init {
        this.data = data
    }

}