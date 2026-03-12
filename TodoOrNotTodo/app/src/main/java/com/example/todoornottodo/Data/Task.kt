package com.example.todoornottodo.Data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoornottodo.utils.Periodicity

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val date: Long,
    val isDone: Boolean,
    val lateNotificationSent: Boolean = false,
    val repeatType: Periodicity,
    val priority: Int = 0,
    val points: Int = 0,
    val taskDescription: String = "",
    val imageUri: String? = null
)