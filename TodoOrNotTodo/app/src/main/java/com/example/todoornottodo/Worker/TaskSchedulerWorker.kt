package com.example.todoornottodo.Worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoornottodo.Data.AppDatabase
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.Data.TaskDao
import com.example.todoornottodo.utils.Periodicity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class TaskSchedulerWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "task_channel"
        private const val CHANNEL_NAME = "Task Notifications"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        try {

            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.taskDao()

            val now = System.currentTimeMillis()
            val tasks = dao.getAllTasksOnce()

            tasks.forEach { task ->

                if (!task.isDone) {
                    sendNotification(task.title)
                }

                if (task.repeatType != Periodicity.NONE) {

                    val nextDate = getNextOccurrence(task)

                    if (nextDate != null) {

                        dao.update(
                            task.copy(
                                date = nextDate,
                                isDone = false,
                                lateNotificationSent = false
                            )
                        )
                    }
                }
            }

            Result.success()

        } catch (e: Exception) {

            e.printStackTrace()
            Result.failure()

        }
    }

    private fun sendNotification(title: String) {

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Tâche en retard")
            .setContentText(title)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun getNextOccurrence(task: Task): Long? {

        var next = task.date
        val now = System.currentTimeMillis()

        while (next <= now) {

            next = when (task.repeatType) {

                Periodicity.DAILY ->
                    next + 24 * 60 * 60 * 1000

                Periodicity.WEEKLY ->
                    next + 7 * 24 * 60 * 60 * 1000

                Periodicity.MONTHLY -> {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = next
                    cal.add(Calendar.MONTH, 1)
                    cal.timeInMillis
                }

                Periodicity.NONE -> return null
            }
        }

        return next
    }
}