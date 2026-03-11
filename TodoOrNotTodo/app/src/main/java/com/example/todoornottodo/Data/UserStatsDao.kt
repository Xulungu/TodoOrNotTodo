package com.example.todoornottodo.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserStatsDao {

    @Query("SELECT * FROM user_stats WHERE id = 0")
    suspend fun getStats(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: UserStats)
}