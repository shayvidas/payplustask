package com.payplus.billing.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility object for formatting dates and timestamps.
 */
object DateFormatter {
    
    /**
     * Date format pattern as specified: dd-MM-yyyy HH:mm
     */
    private const val DATE_FORMAT_PATTERN = "dd-MM-yyyy HH:mm"
    
    /**
     * SimpleDateFormat instance for formatting dates.
     * Uses the specified pattern and default locale.
     */
    private val dateFormat = SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault())
    
    /**
     * Formats a Unix timestamp (milliseconds) into a human-readable date string.
     * 
     * @param timestamp Unix timestamp in milliseconds
     * @return Formatted date string in the format "dd-MM-yyyy HH:mm"
     * 
     * Example: 1729351200000L -> "19-10-2026 15:30"
     */
    fun formatTimestamp(timestamp: Long): String {
        return try {
            dateFormat.format(Date(timestamp))
        } catch (e: Exception) {
            "Invalid date"
        }
    }
}
