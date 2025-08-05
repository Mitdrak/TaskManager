package com.example.taskmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.data.local.mapper.TimestampConverters

@Database(entities = [TaskEntity::class], version = 2, exportSchema = false)
@TypeConverters(TimestampConverters::class)
abstract class AppDatabase: RoomDatabase(){
    abstract fun taskDao(): TaskDao
}
