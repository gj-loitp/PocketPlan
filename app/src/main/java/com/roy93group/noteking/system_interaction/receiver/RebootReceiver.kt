package com.roy93group.noteking.system_interaction.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.roy93group.noteking.system_interaction.handler.notifications.AlarmHandler
import com.jakewharton.threetenabp.AndroidThreeTen
import com.roy93group.noteking.data.settings.SettingId
import com.roy93group.noteking.data.settings.SettingsManager
import com.roy93group.noteking.data.sleepreminder.SleepReminder
import com.roy93group.noteking.system_interaction.handler.storage.StorageHandler

class RebootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AndroidThreeTen.init(context)
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            StorageHandler.path = context.filesDir.absolutePath
            SettingsManager.init()
            AlarmHandler.run {
                val time = SettingsManager.getSetting(SettingId.BIRTHDAY_NOTIFICATION_TIME) as String
                setBirthdayAlarms(time, context = context)
            }
            SleepReminder(context).updateReminder()
        }
    }
}