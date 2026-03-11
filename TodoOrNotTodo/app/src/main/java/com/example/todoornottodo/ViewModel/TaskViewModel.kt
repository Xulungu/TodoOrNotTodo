package com.example.todoornottodo.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoornottodo.Data.AppDatabase
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.utils.Periodicity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).taskDao()

    val tasks = dao.getAllTasks()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addTask(title: String, date: Long, repeatType: Periodicity, priority: Int) {
        viewModelScope.launch {
            dao.insert(
                Task(
                    title = title,
                    date = date,
                    isDone = false,
                    repeatType = repeatType,
                    priority = priority
                )
            )
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.delete(task)
        }
    }

    fun updateTask(task: Task, done: Boolean) {
        viewModelScope.launch {
            dao.update(task)
        }
    }

    fun toggleTaskDone(task: Task) {
        viewModelScope.launch {
            dao.update(task.copy(isDone = !task.isDone))
        }
    }
}