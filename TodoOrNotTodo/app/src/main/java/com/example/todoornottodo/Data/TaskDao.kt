package com.example.todoornottodo.Data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)


    @Query("SELECT * FROM tasks WHERE repeatType != 'NONE'")
    suspend fun getPeriodicTasks(): List<Task>

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksOnce(): List<Task>

    @Query("SELECT * FROM tasks WHERE date < :now")
    suspend fun getLateTasks(now: Long): List<Task>
}