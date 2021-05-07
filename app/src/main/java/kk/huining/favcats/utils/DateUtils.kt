package kk.huining.favcats.utils

import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

fun getFormattedDateTimeInLocal(iso8601String: String?): String {
    if (iso8601String == null) return ""
    val date = parseISO8601DateTimeString(iso8601String)
    return getFormattedDateTimeInLocal(date)
}

private fun getFormattedDateTimeInLocal(date: Date?): String {
    if (date == null) return ""
    //val dateFormat = DateFormat.getDateInstance(
    val dateFormat = SimpleDateFormat("h:mm a, MMM dd", Locale.getDefault())
    return dateFormat.format(date)
}

// Parse ISO 8601 date-time string.
// Note: When the min API level is raised to 26, please replace SimpleDateFormat by DateTimeFormatter,
// and Date by LocalDateTime.
private fun parseISO8601DateTimeString(dateInString: String?): Date? {
    if (dateInString != null) {
        val sdf = SimpleDateFormat(ISO_8601_PATTERN, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            sdf.parse(dateInString)
        } catch (e: ParseException) {
            Timber.e(e,"parseISO8601DateTimeString: Caught ParseException")
            null
        }
    }
    return null
}
