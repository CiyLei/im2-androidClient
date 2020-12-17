package com.dj.im

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.dj.im.sdk.DJIM
import com.umeng.message.UmengMessageHandler
import com.umeng.message.entity.UMessage

/**
 * Create by ChenLei on 2020/12/14
 * Describe:
 */
class ImMessageHandler(val context: Context) : UmengMessageHandler() {

    companion object {
        private const val CHANNEL_ID = "im_channel_id"
        private const val CHANNEL_NAME = "im_channel_name"

        fun notificationMessage(context: Context, id: Int, title: String, content: String) {
            val notification = notification(context, title, content)
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            manager?.notify(id, notification)
        }

        private fun notification(context: Context, title: String, content: String): Notification {
            //  android8.0 以上要创建一个通道
            var channel: NotificationChannel? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel =
                    NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                channel.enableLights(true);//是否在桌面icon右上角展示小红点
                channel.lightColor = Color.RED;//小红点颜色
                channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
            }
            // 创建通知
            val builder: NotificationCompat.Builder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationCompat.Builder(context, CHANNEL_ID)
                } else {
                    NotificationCompat.Builder(context)
                }
            builder.setContentTitle(title)
            builder.setContentText(content)
            builder.setChannelId(CHANNEL_ID)
            builder.setAutoCancel(true)
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setWhen(System.currentTimeMillis())
            builder.setDefaults(NotificationCompat.DEFAULT_ALL)
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            // android8.0 以上，创建通道
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager?.createNotificationChannel(channel!!)
            }
            return builder.build()
        }
    }

    override fun getNotification(p0: Context?, p1: UMessage?): Notification {
        // 代表友盟im自定义推送
        if (p1?.builder_id == DJIM.UMENG_IM_BUILDER_ID) {
            return notification(context, p1.title, p1.text)
        }
        return super.getNotification(p0, p1)
    }


}