package com.example.taskmanager.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.taskmanager.domain.model.Task
import com.google.firebase.Timestamp
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    private val TIME_FORMATS = listOf(
        SimpleDateFormat("hh:mm a", Locale.getDefault()), // For "04:30 PM", "10:00 AM"
        SimpleDateFormat("HH:mm", Locale.getDefault())    // For "16:30", "08:00"
    )
    private const val TIME_FORMAT_STRING = "hh:mm a"
    fun convertTimeToMillis(timeString: String?, firebaseTimestamp: Timestamp?): Long? {
        if (timeString == null || firebaseTimestamp == null) {
            Timber.w("convertTimeToMillis received null input: timeString=$timeString, firebaseTimestamp=$firebaseTimestamp")
            return null
        }

        try {
            // 1. Get the date part from the Firebase Timestamp
            val dateFromFirebase: Date = firebaseTimestamp.toDate() // This gives you a java.util.Date object

            // 2. Parse the time string into a Date object (this will use a default date like Jan 1, 1970)
            var parsedTime: Date? = null
            var parseException: ParseException? = null

            // Try parsing with different formats
            for (sdf in TIME_FORMATS) {
                try {
                    parsedTime = sdf.parse(timeString)
                    if (parsedTime != null) {
                        break // Successfully parsed, exit loop
                    }
                } catch (e: ParseException) {
                    parseException = e // Store the last exception if all fail
                }
            }

            if (parsedTime == null) {
                Timber.e(parseException, "Failed to parse time string '$timeString' with available formats.")
                return null
            }

            // 3. Create a Calendar instance and set its date to the date from Firebase
            val calendar = Calendar.getInstance()
            calendar.time = dateFromFirebase // Set the calendar to the date from Firebase Timestamp
            calendar.timeZone = TimeZone.getDefault() // Ensure it respects local timezone

            // 4. Set the hour, minute, second, and millisecond from the parsed time into the calendar
            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = parsedTime // Use the parsed time here
            timeCalendar.timeZone = TimeZone.getDefault() // Use the same timezone

            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0) // Reset seconds
            calendar.set(Calendar.MILLISECOND, 0) // Reset milliseconds

            // 5. Return the combined timestamp in milliseconds
            return calendar.timeInMillis

        } catch (e: Exception) { // Catch broader exceptions that might occur during date operations
            Timber.e(e, "An error occurred during convertTimeToMillis for timeString: $timeString, firebaseTimestamp: $firebaseTimestamp")
            return null
        }
    }
}

data class TimeBlockInfo(
    val startTime: String,
    val endTime: String,
    val timeHeaderformatted: String = "",
    val showTimeHeader: Boolean,
    val timeHeader: Int = 0
)
