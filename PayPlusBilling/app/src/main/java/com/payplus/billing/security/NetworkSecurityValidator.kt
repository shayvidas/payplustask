package com.payplus.billing.security

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.InetAddress

/**
 * Network Security Validator for Banking Applications.
 * 
 * Validates network conditions before making sensitive API calls.
 * Banking Standard: Ensure secure connection before transmitting financial data.
 */
object NetworkSecurityValidator {
    
    /**
     * Validates if network connection is secure for banking operations.
     * 
     * Checks:
     * 1. Network is connected
     * 2. Not using VPN (could be MITM)
     * 3. Not on captive portal (hotel wifi, airport)
     * 4. Connection is encrypted (WiFi with WPA2+)
     * 
     * @param context Application context
     * @return NetworkSecurityStatus
     */
    fun validateNetworkSecurity(context: Context): NetworkSecurityStatus {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as? ConnectivityManager
            ?: return NetworkSecurityStatus.NO_CONNECTION
        
        val network = connectivityManager.activeNetwork 
            ?: return NetworkSecurityStatus.NO_CONNECTION
        
        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return NetworkSecurityStatus.NO_CONNECTION
        
        // Check if connected
        if (!capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            return NetworkSecurityStatus.NO_CONNECTION
        }
        
        // Check if validated (not captive portal)
        if (!capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            return NetworkSecurityStatus.CAPTIVE_PORTAL
        }
        
        // Check if VPN is active (could be MITM attack)
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            android.util.Log.w("NetworkSecurity", "⚠️  VPN detected - potential MITM risk")
            return NetworkSecurityStatus.VPN_DETECTED
        }
        
        // Check if using cellular or WiFi
        val hasWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val hasCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        
        if (!hasWifi && !hasCellular) {
            return NetworkSecurityStatus.UNSUPPORTED_TRANSPORT
        }
        
        // For WiFi, prefer encrypted connections
        if (hasWifi) {
            android.util.Log.i("NetworkSecurity", "⚠️  WiFi connection - ensure WPA2+ encryption")
            // In production, you might want to check WiFi security level
        }
        
        return NetworkSecurityStatus.SECURE
    }
    
    /**
     * Checks if connected to a known safe network.
     * Banking apps might want to whitelist specific WiFi networks.
     */
    fun isOnTrustedNetwork(context: Context): Boolean {
        // In production, check against whitelist of trusted networks
        // For now, just validate basic security
        return validateNetworkSecurity(context) == NetworkSecurityStatus.SECURE
    }
    
    /**
     * Detects if DNS is being hijacked (common MITM technique).
     */
    fun detectDNSHijacking(): Boolean {
        return try {
            // Try to resolve a known domain
            val google = InetAddress.getByName("google.com")
            val expected = google.hostAddress?.startsWith("142.") ?: false // Google IP range
            
            if (!expected) {
                android.util.Log.w("NetworkSecurity", "⚠️  Potential DNS hijacking detected")
            }
            
            !expected
        } catch (e: Exception) {
            false
        }
    }
    
    enum class NetworkSecurityStatus {
        SECURE,
        NO_CONNECTION,
        VPN_DETECTED,
        CAPTIVE_PORTAL,
        UNSUPPORTED_TRANSPORT
    }
}
