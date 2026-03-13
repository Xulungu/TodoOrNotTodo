package com.example.todoornottodo.ViewModel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoornottodo.Data.AppDatabase
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.Data.UserStats
import com.example.todoornottodo.utils.ThemeManager
import com.example.todoornottodo.utils.Periodicity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val taskDao = db.taskDao()
    private val userStatsDao = db.userStatsDao()

    // 🔹 Liste des tâches
    val tasks = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 🔹 Ajouter une tâche
    fun addTask(
        title: String,
        date: Long,
        repeatType: Periodicity,
        priority: Int,
        taskDescription: String,
        imageUri: String?
    ) {
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
        viewModelScope.launch { taskDao.delete(task) }
    }

    fun updateTask(task: Task, done: Boolean) {
        viewModelScope.launch {
            val wasDone = task.isDone
            val updatedTask = task.copy(isDone = done)
            taskDao.update(updatedTask)

            if (!wasDone && done) {
                val stats = userStatsDao.getStats() ?: UserStats()
                userStatsDao.insert(stats.copy(totalPoints = stats.totalPoints + task.points))
            }
        }
    }

    suspend fun getTotalPoints(): Int {
        return userStatsDao.getStats()?.totalPoints ?: 0
    }

    fun refreshPeriodicTasks() {
        viewModelScope.launch {
            val tasksList = taskDao.getAllTasksOnce()
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
        if (task.date > now) return null
        val cal = Calendar.getInstance()
        cal.timeInMillis = task.date
        when (task.repeatType) {
            Periodicity.DAILY -> cal.add(Calendar.DAY_OF_YEAR, 1)
            Periodicity.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            Periodicity.MONTHLY -> cal.add(Calendar.MONTH, 1)
            else -> return null
        }
        return cal.timeInMillis
    }

    // 🔹 Points
    fun spendPoints(amount: Int) {
        viewModelScope.launch {
            val stats = userStatsDao.getStats() ?: UserStats()
            val newPoints = (stats.totalPoints - amount).coerceAtLeast(0)
            userStatsDao.insert(stats.copy(totalPoints = newPoints))
        }
    }

    fun resetPoints() {
        viewModelScope.launch {
            val stats = userStatsDao.getStats() ?: UserStats()
            userStatsDao.insert(stats.copy(totalPoints = 0))
        }
    }

    // 🔹 Son Faaaah
    suspend fun isFaahPurchased(): Boolean = userStatsDao.getStats()?.faahPurchased ?: false
    fun buyFaah() {
        viewModelScope.launch {
            val stats = userStatsDao.getStats() ?: UserStats()
            userStatsDao.insert(stats.copy(faahPurchased = true))
        }
    }

    // 🔹 Sons et son custom
    private var selectedSound: String = "default"
    private var soundEnabled: Boolean = true
    private var customSoundUri: Uri? = null

    fun setSelectedSound(sound: String) { selectedSound = sound }
    fun getSelectedSound(): String = selectedSound

    fun setSoundEnabled(enabled: Boolean) { soundEnabled = enabled }
    fun isSoundEnabled(): Boolean = soundEnabled

    fun setCustomSoundUri(uri: Uri) { customSoundUri = uri }
    fun getCustomSoundUri(): Uri? = customSoundUri

    // 🔹 Thème aléatoire
    private var randomThemeUnlocked: Boolean = false
    fun unlockRandomTheme() { randomThemeUnlocked = true }
    fun isRandomThemeUnlocked(): Boolean = randomThemeUnlocked

    // 🔹 Thème dynamique
    var currentColorScheme by mutableStateOf(ThemeManager.randomLightColorScheme())

    fun applyRandomTheme() {
        if (!isRandomThemeUnlocked()) return

        currentColorScheme = if (isDarkMode()) {
            ThemeManager.randomDarkColorScheme()
        } else {
            ThemeManager.randomLightColorScheme()
        }
    }

    // 🔹 Mode sombre
    private var darkMode = false
    fun setDarkMode(enabled: Boolean) {
        darkMode = enabled
        applyRandomTheme()
    }
    fun isDarkMode(): Boolean = darkMode
}