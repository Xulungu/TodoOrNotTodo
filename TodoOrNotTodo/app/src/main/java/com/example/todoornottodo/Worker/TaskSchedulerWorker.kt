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

                if (!task.isDone && task.date <= now) {
                    sendNotification(task.title)
                }

                if (task.repeatType != Periodicity.NONE) {
                    val nextDate = calculateNextDate(task)
                    if (nextDate != null && nextDate != task.date) {
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

    private fun calculateNextDate(task: Task): Long? {
        if (task.repeatType == Periodicity.NONE) return null

        val cal = Calendar.getInstance().apply { timeInMillis = task.date }
        val now = Calendar.getInstance()

        when (task.repeatType) {
            Periodicity.DAILY -> {
                while (cal.get(Calendar.YEAR) < now.get(Calendar.YEAR) ||
                    cal.get(Calendar.DAY_OF_YEAR) <= now.get(Calendar.DAY_OF_YEAR)
                ) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            Periodicity.WEEKLY -> {
                while (cal.timeInMillis <= now.timeInMillis) {
                    cal.add(Calendar.WEEK_OF_YEAR, 1)
                }
            }
            Periodicity.MONTHLY -> {
                while (cal.timeInMillis <= now.timeInMillis) {
                    cal.add(Calendar.MONTH, 1)
                }
            }
            else -> return null
        }

        return cal.timeInMillis
    }
}