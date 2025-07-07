package com.example.taskmanager.util

import com.example.taskmanager.domain.model.Task

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
}
data class TimeBlockInfo(
    val startTime: String,
    val endTime: String,
    val timeHeaderformatted: String = "",
    val showTimeHeader: Boolean,
    val timeHeader: Int = 0
)
