package com.example.todoornottodo.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlin.random.Random

object NotificationHelper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun showLateTaskNotification(context: Context, title: String) {

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "task_channel"

        val channel = NotificationChannel(
            channelId,
            "Task Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Tâche en retard")
            .setContentText("La tâche \"$title\" est maintenant en retard")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .build()

        manager.notify(Random.nextInt(), notification)
    }
}