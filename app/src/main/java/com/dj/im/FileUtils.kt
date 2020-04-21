package com.dj.im

import android.content.Context
import android.database.Cursor
import android.net.Uri
import java.net.URISyntaxException


/**
 * Create by ChenLei on 2020/4/21
 * Describe:
 */
object FileUtils {
    @Throws(URISyntaxException::class)
    fun getPath(context: Context, uri: Uri): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf("_data")
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                val column_index: Int = cursor.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) { // Eat it  Or Log it.
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }
}