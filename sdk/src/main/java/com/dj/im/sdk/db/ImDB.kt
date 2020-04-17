package com.dj.im.sdk.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Create by ChenLei on 2020/4/17
 * Describe: 数据库帮助类
 */
internal class ImDB private constructor(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, VERSION) {

    companion object {
        // 数据库名称
        private const val DB_NAME = "djim.db"
        // 数据库版本
        private const val VERSION = 1
        // 单例获取
        private var mInstance: ImDB? = null

        fun instance(context: Context): ImDB {
            if (mInstance == null) {
                synchronized(ImDB::class.java) {
                    if (mInstance == null) {
                        mInstance = ImDB(context)
                    }
                }
            }
            return mInstance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE `Message`  (\n" +
                    "  `userId` bigint(0) NOT NULL ,\n" +
                    "  `id` bigint(0) NOT NULL ,\n" +
                    "  `conversationId` char(32) NULL ,\n" +
                    "  `conversationType` int(0) NULL ,\n" +
                    "  `type` int(0) NULL ,\n" +
                    "  `fromId` bigint(0) NULL ,\n" +
                    "  `toId` bigint(0) NULL ,\n" +
                    "  `data` varchar(11700) NULL ,\n" +
                    "  `summary` varchar(10000) NULL ,\n" +
                    "  `createTime` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ,\n" +
                    "  PRIMARY KEY (`userId`, `id`)\n" +
                    ");"
        )
        db?.execSQL("CREATE INDEX userId_conversationId_index ON Message (userId,conversationId);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}