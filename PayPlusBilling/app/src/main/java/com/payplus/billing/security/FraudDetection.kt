package com.payplus.billing.security

import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import java.util.concurrent.ConcurrentHashMap

/**
 * Fraud Detection for Banking Applications.
 * 
 * Detects suspicious behavior patterns that might indicate fraud:
 * - Rapid actions (automated bots)
 * - Location spoofing
 * - Time-based anomalies
 * - Unusual patterns
 */
object FraudDetection {
    
    private val actionTimestamps = ConcurrentHashMap<String, MutableList<Long>>()
    private const val RAPID_ACTION_THRESHOLD_MS = 500L // 500ms between actions
    private const val RAPID_ACTION_COUNT = 5 // 5 actions in short time = suspicious
    
    /**
     * Detects if user is performing actions too quickly (bot behavior).
     * 
     * Banking fraud: Automated bots try to enumerate card numbers,
     * perform rapid transactions, or scrape data.
     * 
     * @param actionType Type of action (e.g., "delete", "view", "list")
     * @return true if behavior is suspicious
     */
    fun detectRapidActions(actionType: String): Boolean {
        val now = System.currentTimeMillis()
        val timestamps = actionTimestamps.getOrPut(actionType) { mutableListOf() }
        
        // Add current timestamp
        timestamps.add(now)
        
        // Remove old timestamps (older than 5 seconds)
        timestamps.removeAll { now - it > 5000 }
        
        // Check if too many actions in short time
        if (timestamps.size >= RAPID_ACTION_COUNT) {
            val timeDiff = now - timestamps.first()
            if (timeDiff < RAPID_ACTION_THRESHOLD_MS * RAPID_ACTION_COUNT) {
                android.util.Log.w("FraudDetection", "⚠️  Rapid actions detected: $actionType")
                return true
            }
        }
        
        return false
    }
    
    /**
     * Detects if location services are being spoofed.
     * Fraudsters use fake GPS to bypass location-based restrictions.
     * 
     * @param context Application context
     * @return true if location spoofing detected
     */
    fun detectLocationSpoofing(context: Context): Boolean {
        // Check if "Allow mock locations" is enabled
        val isMockEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ALLOW_MOCK_LOCATION
            ) == "1"
        } else {
            @Suppress("DEPRECATION")
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ALLOW_MOCK_LOCATION
            ) != "0"
        }
        
        if (isMockEnabled) {
            android.util.Log.w("FraudDetection", "⚠️  Mock location enabled")
            return true
        }
        
        // Check if location providers look suspicious
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) 
            as? LocationManager
        
        locationManager?.let { lm ->
            val providers = lm.allProviders
            // If GPS is disabled but network location is enabled, might be spoofed
            val hasGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val hasNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            
            if (!hasGPS && hasNetwork) {
                android.util.Log.w("FraudDetection", "⚠️  Suspicious location setup")
            }
        }
        
        return false
    }
    
    /**
     * Detects unusual time-based patterns.
     * 
     * Banking fraud indicators:
     * - Transactions at unusual hours (3am-5am)
     * - Device timezone doesn't match location
     * - Rapid timezone changes
     */
    fun detectUnusualTiming(): FraudRisk {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        
        // Flag suspicious hours (2am-5am)
        if (hour in 2..5) {
            android.util.Log.w("FraudDetection", "⚠️  Activity during suspicious hours: ${hour}:00")
            return FraudRisk.MEDIUM
        }
        
        return FraudRisk.LOW
    }
    
    /**
     * Detects if app behavior is automated (not human).
     * 
     * Indicators:
     * - Perfect timing (actions every exactly N seconds)
     * - No variation in behavior
     * - Superhuman speed
     */
    fun detectAutomatedBehavior(actionType: String): Boolean {
        val timestamps = actionTimestamps[actionType] ?: return false
        
        if (timestamps.size < 3) return false
        
        // Check if actions are too perfectly timed (bot behavior)
        val intervals = timestamps.zipWithNext { a, b -> b - a }
        val avgInterval = intervals.average()
        val variance = intervals.map { kotlin.math.abs(it - avgInterval) }.average()
        
        // If variance is very low, actions are too consistent = bot
        if (variance < 10 && avgInterval < 1000) {
            android.util.Log.w("FraudDetection", "⚠️  Automated behavior detected: perfect timing")
            return true
        }
        
        return false
    }
    
    /**
     * Aggregates all fraud signals into risk score.
     * 
     * @return Overall fraud risk level
     */
    fun calculateFraudRisk(
        context: Context,
        actionType: String = "general"
    ): FraudRisk {
        var riskScore = 0
        
        if (detectRapidActions(actionType)) riskScore += 3
        if (detectLocationSpoofing(context)) riskScore += 2
        if (detectUnusualTiming() == FraudRisk.MEDIUM) riskScore += 1
        if (detectAutomatedBehavior(actionType)) riskScore += 3
        
        return when {
            riskScore >= 5 -> FraudRisk.HIGH
            riskScore >= 3 -> FraudRisk.MEDIUM
            else -> FraudRisk.LOW
        }
    }
    
    /**
     * Clears fraud detection data (call on logout).
     */
    fun clearFraudData() {
        actionTimestamps.clear()
    }
    
    enum class FraudRisk {
        LOW,
        MEDIUM,
        HIGH
    }
}
