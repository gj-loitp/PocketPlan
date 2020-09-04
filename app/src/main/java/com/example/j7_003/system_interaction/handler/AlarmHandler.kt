package com.example.j7_003.system_interaction.handler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.j7_003.MainActivity
import com.example.j7_003.system_interaction.receiver.NotificationReceiver
import org.threeten.bp.*

class AlarmHandler {
    companion object {
        fun setBirthdayAlarms(hour: Int = 12, minute: Int = 0, context: Context) {
            val intent = Intent(context, NotificationReceiver::class.java)
            intent.putExtra("Notification", "Birthday")

            val pendingIntent =
                PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager =
                context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

            var notificationTime = LocalDateTime.now()

            if (notificationTime.hour >= hour) {
                if (notificationTime.minute >= minute) {
                    notificationTime = notificationTime.plusDays(1)
                }
            }

            notificationTime = notificationTime
                .withHour(hour).withMinute(minute)
                .withSecond(0).withNano(0)

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

            Log.e("debug", notificationTime.atZone(ZoneId.systemDefault()).toInstant().toString())
        }

        fun setNewSleepReminderAlarm(
            context: Context = MainActivity.act,
            dayOfWeek: DayOfWeek,
            reminderTime: LocalDateTime,
            requestCode: Int,
            isSet: Boolean
        ) {
            val intent = Intent(context, NotificationReceiver::class.java)
            intent.putExtra("Notification", "SReminder")
            intent.putExtra("weekday", dayOfWeek.toString())
            intent.putExtra("requestCode", requestCode)

            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager: AlarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (isSet) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    pendingIntent
                )
            } else {
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}