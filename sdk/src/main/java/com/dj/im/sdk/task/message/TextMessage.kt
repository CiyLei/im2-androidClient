package com.dj.im.sdk.task.message

/**
 * Create by ChenLei on 2020/4/15
 * Describe: 文字消息实体类
 */
class TextMessage(data: String) : Message() {

    init {
        this.data = data
    }

}