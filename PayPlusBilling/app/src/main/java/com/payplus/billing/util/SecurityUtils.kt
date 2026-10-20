package com.payplus.billing.util

import android.content.Context
import android.os.Build
import java.io.File

/**
 * Security utilities for banking-grade protection.
 * 
 * Implements PCI-DSS and financial industry security standards.
 */
object SecurityUtils {
    
    /**
     * Masks a card number to show only last 4 digits (PCI-DSS compliant).
     * 
     * Example:
     * - Input: "4532015112830366"
     * - Output: "**** **** **** 0366"
     * 
     * PCI-DSS Requirement 3.3: Mask PAN (Primary Account Number) when displayed.
     * Only last 4 digits may be shown.
     * 
     * @param cardNumber Full card number
     * @return Masked card number showing only last 4 digits
     */
    fun maskCardNumber(cardNumber: String?): String {
        if (cardNumber.isNullOrBlank()) return "****"
        
        // Remove all non-digits
        val digitsOnly = cardNumber.filter { it.isDigit() }
        
        if (digitsOnly.length < 4) return "****"
        
        // Get last 4 digits
        val last4 = digitsOnly.takeLast(4)
        
        // Format as: **** **** **** 1234
        return "**** **** **** $last4"
    }
    
    /**
     * Masks sensitive data for logging (only shows first 2 and last 2 chars).
     * Used for approval numbers, voucher numbers, etc.
     * 
     * @param data Sensitive string to mask
     * @return Masked string
     */
    fun maskSensitiveData(data: String?): String {
        if (data.isNullOrBlank()) return "N/A"
        if (data.length <= 4) return "****"
        
        val first2 = data.take(2)
        val last2 = data.takeLast(2)
        val middleLength = data.length - 4
        
        return "$first2${"*".repeat(middleLength)}$last2"
    }
    
    /**
     * Detects if device is rooted (compromised security).
     * Rooted devices have full filesystem access and can:
     * - Extract app data
     * - Intercept SSL traffic
     * - Bypass app protections
     * 
     * @return true if device appears rooted, false otherwise
     */
    fun isDeviceRooted(): Boolean {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3()
    }
    
    /**
     * Check for common root files.
     */
    private fun checkRootMethod1(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        
        return paths.any { File(it).exists() }
    }
    
    /**
     * Check for root management apps.
     */
    private fun checkRootMethod2(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/data/data/com.noshufou.android.su",
            "/data/data/com.thirdparty.superuser",
            "/data/data/eu.chainfire.supersu",
            "/data/data/com.koushikdutta.superuser",
            "/data/data/com.zachspong.temprootremovejb",
            "/data/data/com.ramdroid.appquarantine"
        )
        
        return paths.any { File(it).exists() }
    }
    
    /**
     * Check if 'su' command is accessible.
     */
    private fun checkRootMethod3(): Boolean {
        val process: Process? = try {
            Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
        } catch (e: Exception) {
            null
        }
        
        return process != null
    }
    
    /**
     * Detects if device is in developer mode.
     * Developer mode can enable USB debugging, allowing data extraction.
     * 
     * @param context Application context
     * @return true if developer mode is enabled
     */
    fun isDeveloperModeEnabled(context: Context): Boolean {
        return try {
            android.provider.Settings.Secure.getInt(
                context.contentResolver,
                android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) != 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Detects if device is an emulator.
     * Emulators are often used for reverse engineering and testing attacks.
     * 
     * @return true if running on emulator
     */
    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT)
    }
    
    /**
     * Validates that a price is reasonable for a financial transaction.
     * Prevents integer overflow, float precision issues, and fraudulent amounts.
     * 
     * @param price Price to validate
     * @return true if price is valid and reasonable
     */
    fun isReasonableAmount(price: Double?): Boolean {
        if (price == null) return false
        if (price.isNaN() || price.isInfinite()) return false
        if (price < 0.0) return false
        if (price > 1_000_000.0) return false // Max 1 million per transaction
        
        // Check for floating point precision issues
        val cents = (price * 100).toLong()
        val reconstructed = cents / 100.0
        
        // If reconstruction differs significantly, there's a precision issue
        return kotlin.math.abs(price - reconstructed) < 0.001
    }
    
    /**
     * Sanitizes numeric input to prevent injection through numeric fields.
     * 
     * @param value Numeric value as string
     * @return Sanitized value
     */
    fun sanitizeNumericInput(value: String?): String {
        if (value.isNullOrBlank()) return "0"
        
        // Remove all non-digit and non-decimal characters
        return value.filter { it.isDigit() || it == '.' }
    }
}
