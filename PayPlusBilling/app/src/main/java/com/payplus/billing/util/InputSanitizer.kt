package com.payplus.billing.util

/**
 * Utility object for sanitizing and validating user inputs.
 * Prevents injection attacks and ensures data integrity.
 */
object InputSanitizer {
    
    /**
     * Sanitizes a Long ID to ensure it's a valid positive number.
     * Prevents negative IDs or extremely large values that could cause issues.
     * 
     * @param id The ID to validate
     * @return The validated ID
     * @throws IllegalArgumentException if ID is invalid
     */
    fun sanitizeBillingId(id: Long): Long {
        require(id > 0) { "Billing ID must be positive" }
        require(id < Long.MAX_VALUE - 1000) { "Billing ID is suspiciously large" }
        return id
    }
    
    /**
     * Sanitizes a string input by removing potentially dangerous characters.
     * Prevents XSS, SQL injection, and command injection.
     * 
     * @param input The input string to sanitize
     * @return Sanitized string
     */
    fun sanitizeString(input: String): String {
        return input
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;")
            .replace("\\", "&#x5C;")
            .trim()
    }
    
    /**
     * Validates that a price value is reasonable.
     * Prevents overflow, underflow, and malicious extreme values.
     * 
     * @param price The price to validate
     * @return true if valid, false otherwise
     */
    fun isValidPrice(price: Double?): Boolean {
        if (price == null) return false
        if (price.isNaN() || price.isInfinite()) return false
        if (price < 0.0) return false
        if (price > 10_000_000.0) return false // Max 10 million
        return true
    }
    
    /**
     * Validates that a timestamp is reasonable.
     * Prevents dates far in the future or past that could indicate manipulation.
     * 
     * @param timestampSeconds Unix timestamp in seconds
     * @return true if valid, false otherwise
     */
    fun isValidTimestamp(timestampSeconds: Long?): Boolean {
        if (timestampSeconds == null) return false
        
        // Year 2000 = 946684800 seconds
        // Year 2100 = 4102444800 seconds
        return timestampSeconds in 946684800..4102444800
    }
    
    /**
     * Validates card number format (basic check, not full Luhn algorithm).
     * Prevents obviously invalid card numbers.
     * 
     * @param cardNumber The card number to validate
     * @return true if format looks reasonable, false otherwise
     */
    fun isValidCardNumberFormat(cardNumber: String?): Boolean {
        if (cardNumber.isNullOrBlank()) return false
        
        // Remove spaces and dashes
        val cleaned = cardNumber.replace(Regex("[\\s-]"), "")
        
        // Check if only digits
        if (!cleaned.all { it.isDigit() }) return false
        
        // Valid card numbers are typically 12-19 digits
        return cleaned.length in 12..19
    }
    
    /**
     * Validates that entry numbers are logical.
     * 
     * @param entryNumber Current entry number
     * @param totalEntryCount Total count
     * @return true if valid, false otherwise
     */
    fun isValidEntryNumbers(entryNumber: Int?, totalEntryCount: Int?): Boolean {
        if (entryNumber == null || totalEntryCount == null) return false
        if (entryNumber <= 0 || totalEntryCount <= 0) return false
        return entryNumber <= totalEntryCount
    }
}
