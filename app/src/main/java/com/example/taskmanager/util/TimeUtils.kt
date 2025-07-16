package com.example.taskmanager.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.taskmanager.domain.model.Task
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeUtils {
    fun parseTimeToMinutes(time: String): Int {
        val parts = time.split(":")
        return parts[0].toInt() * 60 + parts[1].toInt()
    }

    fun formatMinutesToTime(minutes: Int): String {
        return "%02d:%02d".format(minutes / 60, minutes % 60)
    }

    fun formatTimeWithAmPm(time: String): String {
        val hour = time.split(":")[0].toInt()
        val period = if (hour < 12) "AM" else "PM"
        return "$time $period"
    }

    fun getEndTimeForTimeBlock(startTimeMinutes: Int, durationMinutes: Int = 300): Int {
        return (startTimeMinutes + durationMinutes)
    }

    fun calculateTimeBlockInfo(events: List<Task>, currentIndex: Int, endHourTitleInt: Int): TimeBlockInfo {
        val currentEvent = events[currentIndex]
        val previousTaskStartMinutes = events.getOrNull(currentIndex - 1)?.timeStart?.let {
            parseTimeToMinutes(it)
        }
        val taskHourEndInt = parseTimeToMinutes(currentEvent.timeEnd)
        val taskHourStartInt = parseTimeToMinutes(currentEvent.timeStart)

        val isSameRangeTime = previousTaskStartMinutes == null || taskHourEndInt > endHourTitleInt
        var final = endHourTitleInt
        if (isSameRangeTime) {
            final = getEndTimeForTimeBlock(taskHourStartInt)
        }
        return TimeBlockInfo(
            startTime = formatTimeWithAmPm(currentEvent.timeStart),
            endTime = formatTimeWithAmPm(currentEvent.timeEnd),
            timeHeader = final,
            timeHeaderformatted = formatTimeWithAmPm(formatMinutesToTime(final)),
            showTimeHeader = isSameRangeTime,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertMillisToDate(millis: Long): String {
        val zone = ZoneId.systemDefault()
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(zone)
        val localDate = LocalDate.ofEpochDay(
            millis / (24 * 60 * 60 * 1000) // Convert millis to days
        )
        val instant = localDate.atStartOfDay(zone).toInstant()
        return formatter.format(instant)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertStringToTimestamp(dateString: String): Timestamp {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy") // or "dd/MM/yyyy"
        val localDate = LocalDate.parse(dateString, formatter)
        val instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        return Timestamp(instant.epochSecond, instant.nano)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTimestampToString(timestamp: Timestamp): String {
        val zone = ZoneId.systemDefault()
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(zone)

        return formatter.format(Instant.ofEpochMilli(timestamp.toDate().time))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertStringToMillis(dateString: String): Long {
        val zone = ZoneId.systemDefault()
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(zone)
        val localDate = LocalDate.parse(dateString, formatter)
        return localDate.atStartOfDay(zone).toInstant().toEpochMilli()
    }
}

data class TimeBlockInfo(
    val startTime: String,
    val endTime: String,
    val timeHeaderformatted: String = "",
    val showTimeHeader: Boolean,
    val timeHeader: Int = 0
)
