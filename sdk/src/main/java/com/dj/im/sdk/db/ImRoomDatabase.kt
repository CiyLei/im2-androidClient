package com.dj.im.sdk.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.dj.im.sdk.entity.*

/**
 * Create by ChenLei on 2020/4/26
 * Describe: im本地数据Room
 */
@Database(
    entities = [ImUser::class, ImMessage::class, ImConversation::class, UnReadMessage::class, ImGroup::class, UserGroupEntity::class],
    version = 1
)
internal abstract class ImRoomDatabase : RoomDatabase() {

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
                        ).allowMainThreadQueries().build()
                    }
                }
            }
            return mInstance!!
        }
    }

    abstract fun imUserDao(): ImUserRoomDao
    abstract fun imGroupDao(): ImGroupRoomDao
    abstract fun imConversationDao(): ImConversationRoomDao
    abstract fun imMessageDao(): ImMessageRoomDao
    abstract fun imUnReadRoomDao(): ImUnReadRoomDao
    abstract fun imUserGroupRoomDao(): ImUserGroupRoomDao
}