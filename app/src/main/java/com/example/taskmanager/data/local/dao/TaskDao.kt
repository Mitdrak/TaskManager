package com.example.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskmanager.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY timeStart DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>//List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks(): Int

    @Query("SELECT * FROM tasks WHERE dateStart >= :startOfDayMillis AND dateStart < :endOfDayMillis ORDER  BY timeStart ASC")
    fun getTaskbyDateRange(startOfDayMillis: Long, endOfDayMillis: Long): Flow<List<TaskEntity>>



    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    fun getTaskById(taskId: String): Flow<TaskEntity>
}
