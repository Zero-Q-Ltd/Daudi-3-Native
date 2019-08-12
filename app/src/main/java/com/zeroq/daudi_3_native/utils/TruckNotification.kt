package com.zeroq.daudi_3_native.utils

import android.R
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class TruckNotification @Inject constructor(var alarmManager: AlarmManager) {

    companion object {
        const val DAILY_REMINDER_REQUEST_CODE = 100
    }

    fun setReminder(
        context: Context, cls: Class<*>, expireDate: Date,
        title: String, content: String
    ) {
        val calendar = Calendar.getInstance()
        val setcalendar = Calendar.getInstance()

        setcalendar.time = expireDate

        if (setcalendar.before(calendar)){
            // current time is greater
            Timber.d("jumped")
            return
        }


        // Enabled a receiver
        val receiver = ComponentName(context, cls)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        val intent1 = Intent(context, cls)
        intent1.putExtra("CONTENT", content)
        intent1.putExtra("TITLE", title)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_REQUEST_CODE, intent1,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, setcalendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, cls: Class<*>) {
        val receiver = ComponentName(context, cls)

        val pm = context.packageManager

        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )

        val intent1 = Intent(context, cls)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

    }


    fun showNotification(context: Context, cls: Class<*>, title: String, content: String) {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationIntent = Intent(context, cls)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(cls)
        stackBuilder.addNextIntent(notificationIntent)

        val pendingIntent = stackBuilder.getPendingIntent(
            DAILY_REMINDER_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context)
        val notification = builder.setContentTitle(title)
            .setContentText(content).setAutoCancel(true)

            .setSound(alarmSound).setSmallIcon(R.mipmap.sym_def_app_icon)

            .setContentIntent(pendingIntent).build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, notification)
    }


}