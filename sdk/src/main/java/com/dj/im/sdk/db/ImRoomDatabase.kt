package com.dj.im.sdk.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.dj.im.sdk.entity.ImConversation
import com.dj.im.sdk.entity.ImMessage
import com.dj.im.sdk.entity.ImUser

/**
 * Create by ChenLei on 2020/4/26
 * Describe: im本地数据Room
 */
@Database(entities = [ImUser::class, ImMessage::class, ImConversation::class], version = 1)
abstract class ImRoomDatabase : RoomDatabase() {

    companion object {
        // 单例获取
        private var mInstance: ImRoomDatabase? = null

        fun instance(context: Context): ImRoomDatabase {
            if (mInstance == null) {
                synchronized(ImRoomDatabase::class.java) {
                    if (mInstance == null) {
                        mInstance = Room.databaseBuilder(
                            context.applicationContext,
                            ImRoomDatabase::class.java, "im_database"
                        ).build()
                    }
                }
            }
            return mInstance!!
        }
    }

    abstract fun imRoomDao(): ImRoomDao
}