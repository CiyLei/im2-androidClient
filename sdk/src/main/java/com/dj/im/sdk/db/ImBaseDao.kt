package com.dj.im.sdk.db

import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Update


/**
 * Create by ChenLei on 2020/12/9
 * Describe: IM基本数据库操作
 */
internal interface ImBaseDao<T> {

    /**
     * 插入单条数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T?)

    /**
     * 插入list数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(items: List<T?>?)

    /**
     * 删除item
     */
    @Delete
    fun delete(item: T?)

    /**
     * 删除item
     */
    @Delete
    fun deleteList(items: List<T?>?)

    /**
     * 更新item
     */
    @Update
    fun update(item: T?)

}