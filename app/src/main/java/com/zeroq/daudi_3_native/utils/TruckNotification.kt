package com.zeroq.daudi_3_native.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import java.util.*
import javax.inject.Inject


class TruckNotification @Inject constructor(var alarmManager: AlarmManager) {

    companion object {
        const val DAILY_REMINDER_REQUEST_CODE = 100
    }

    fun setReminder(context: Context, cls: Class<*>, hour: Int, min: Int, sec: Int) {
        val calendar = Calendar.getInstance()


        val setcalendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.HOUR, hour)
            add(Calendar.MINUTE, min)
            add(Calendar.SECOND, sec)
        }


        // cancel already scheduled reminders
        cancelReminder(context, cls)

        // Enabled a receiver
        val receiver = ComponentName(context, cls)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        val intent1 = Intent(context, cls)
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


}