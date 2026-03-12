package com.example.todoornottodo.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoornottodo.Data.AppDatabase
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.Data.UserStats
import com.example.todoornottodo.utils.Periodicity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val taskDao = db.taskDao()
    private val userStatsDao = db.userStatsDao()

    val tasks = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(title: String, date: Long, repeatType: Periodicity, priority: Int, taskDescription: String, imageUri: String?) {
        viewModelScope.launch {
            val points = (priority.coerceAtMost(10) * 2 + 1)
            val task = Task(
                title = title,
                date = date,
                isDone = false,
                repeatType = repeatType,
                priority = priority,
                points = points,
                taskDescription = taskDescription,
                imageUri = imageUri
            )
            taskDao.insert(task)
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isDone = true)
            taskDao.update(updatedTask)

            val stats = userStatsDao.getStats() ?: UserStats()
            userStatsDao.insert(stats.copy(totalPoints = stats.totalPoints + task.points))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
        }
    }

    fun updateTask(task: Task, done: Boolean) {
        viewModelScope.launch {
            val wasDone = task.isDone
            val updatedTask = task.copy(isDone = done)
            taskDao.update(updatedTask)

            // Ajouter les points si la tâche vient juste d'être complétée
            if (!wasDone && done) {
                val stats = userStatsDao.getStats() ?: UserStats()
                userStatsDao.insert(stats.copy(totalPoints = stats.totalPoints + task.points))
            }
        }
    }

    suspend fun getTotalPoints(): Int {
        return userStatsDao.getStats()?.totalPoints ?: 0
    }

    // Nouvelle fonction pour gérer les tâches périodiques lors de l'ouverture de l'app
    fun refreshPeriodicTasks() {
        viewModelScope.launch {
            val tasksList = taskDao.getAllTasksOnce()
            val now = Calendar.getInstance()

            tasksList.forEach { task ->
                if (task.repeatType != Periodicity.NONE && task.isDone) {
                    val nextDate = calculateNextDate(task)
                    if (nextDate != null && nextDate != task.date) {
                        taskDao.update(task.copy(date = nextDate, isDone = false))
                    }
                }
            }
        }
    }

    private fun calculateNextDate(task: Task): Long? {

        if (task.repeatType == Periodicity.NONE) return null

        val now = System.currentTimeMillis()

        // Si la tâche n'est pas encore dépassée on ne change rien
        if (task.date > now) return null

        val cal = Calendar.getInstance()
        cal.timeInMillis = task.date

        when (task.repeatType) {

            Periodicity.DAILY -> {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }

            Periodicity.WEEKLY -> {
                cal.add(Calendar.WEEK_OF_YEAR, 1)
            }

            Periodicity.MONTHLY -> {
                cal.add(Calendar.MONTH, 1)
            }

            else -> return null
        }

        return cal.timeInMillis
    }
}