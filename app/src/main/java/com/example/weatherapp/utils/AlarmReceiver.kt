package com.example.weatherapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weatherapp.R
import com.example.weatherapp.model.local.AlertType

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private var ringtone: Ringtone? = null
        const val ACTION_STOP_ALARM = "com.example.weatherapp.STOP_ALARM"
        const val NOTIFICATION_ID = 1001

    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_STOP_ALARM -> stopAlarm(context)
            else -> {
                val title = intent.getStringExtra("ALERT_TITLE") ?: "Weather Alert"
                val type = intent.getStringExtra("ALERT_TYPE") ?: AlertType.NOTIFICATION.name

                when (type) {
                    AlertType.NOTIFICATION.name -> showNotificationWithStop(
                        context,
                        title,
                        isAlarm = false
                    )

                    AlertType.ALARM.name -> playAlarm(context, title)
                }
            }
        }
    }


    private fun playAlarm(context: Context, title: String) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            0
        )

        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        stopAlarm(context)

        ringtone = RingtoneManager.getRingtone(context, alarmUri) // Store ringtone globally
        ringtone?.play()

        showNotificationWithStop(context, title, isAlarm = true)
    }

    private fun stopAlarm(context: Context) {
        if (ringtone?.isPlaying == true) {
            ringtone?.stop()
        }
        ringtone = null

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }


    private fun showNotificationWithStop(context: Context, title: String, isAlarm: Boolean) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, "weather_alarms")
            .setSmallIcon(R.drawable.ic_alerts)
            .setContentTitle(title)
            .setContentText("Time to check the weather!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))

        if (isAlarm) {
            val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_STOP_ALARM
            }

            val stopPendingIntent = PendingIntent.getBroadcast(
                context, 0, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            builder.addAction(R.drawable.ic_alerts_unfilled, "Stop Alarm", stopPendingIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_alarms",
                "Weather Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

}
