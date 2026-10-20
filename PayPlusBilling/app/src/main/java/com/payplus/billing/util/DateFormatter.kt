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
     * Date only format pattern: dd.MM.yyyy
     */
    private const val DATE_ONLY_PATTERN = "dd.MM.yyyy"
    
    /**
     * SimpleDateFormat instance for formatting dates.
     * Uses the specified pattern and default locale.
     */
    private val dateFormat = SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault())
    
    /**
     * SimpleDateFormat instance for date only.
     */
    private val dateOnlyFormat = SimpleDateFormat(DATE_ONLY_PATTERN, Locale.getDefault())
    
    /**
     * Formats a Unix timestamp (SECONDS) into a human-readable date string.
     * API returns timestamps in seconds, not milliseconds!
     * 
     * @param timestampSeconds Unix timestamp in SECONDS
     * @return Formatted date string in the format "dd-MM-yyyy HH:mm"
     * 
     * Example: 1592293845L -> "16-06-2020 10:50"
     */
    fun formatTimestamp(timestampSeconds: Long?): String {
        if (timestampSeconds == null) return "N/A"
        return try {
            // Convert seconds to milliseconds for Date constructor
            dateFormat.format(Date(timestampSeconds * 1000L))
        } catch (e: Exception) {
            "Invalid date"
        }
    }
    
    /**
     * Formats a Unix timestamp (SECONDS) into date only string.
     * 
     * @param timestampSeconds Unix timestamp in SECONDS
     * @return Formatted date string in the format "dd.MM.yyyy"
     * 
     * Example: 1592293845L -> "16.06.2020"
     */
    fun formatDate(timestampSeconds: Long?): String {
        if (timestampSeconds == null) return "N/A"
        return try {
            // Convert seconds to milliseconds for Date constructor
            dateOnlyFormat.format(Date(timestampSeconds * 1000L))
        } catch (e: Exception) {
            "Invalid date"
        }
    }
}
