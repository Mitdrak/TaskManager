package com.example.taskmanager.data.local.mapper

import androidx.room.TypeConverter
import com.google.firebase.Timestamp


object TimestampConverters {

    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.toDate()?.time // Convert Firebase Timestamp to Java Date, then to milliseconds Long
    }

    @TypeConverter
    fun toTimestamp(long: Long?): Timestamp? {
        return long?.let {
            Timestamp(java.util.Date(it)) // Convert milliseconds Long back to Java Date, then to Firebase Timestamp
        }
    }
}
