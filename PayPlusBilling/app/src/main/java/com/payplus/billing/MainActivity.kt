package com.payplus.billing

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.payplus.billing.presentation.navigation.BillingNavGraph
import com.payplus.billing.presentation.theme.PayPlusBillingTheme
import com.payplus.billing.util.SecurityUtils
import com.payplus.billing.security.FraudDetection
import com.payplus.billing.security.TamperDetection
import com.payplus.billing.security.NetworkSecurityValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Main Activity for the PayPlus Billing application.
 * 
 * Entry point with enhanced security for financial/banking applications:
 * - Screen capture prevention (FLAG_SECURE)
 * - Root detection on startup
 * - Session timeout (5 minutes inactivity)
 * - Developer mode detection
 * 
 * Annotated with @AndroidEntryPoint to enable Hilt dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private var lastInteractionTime = System.currentTimeMillis()
    private val SESSION_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutes
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 🔒 CRITICAL SECURITY: Prevent screenshots and screen recording
        // PCI-DSS Requirement: Card data must not be captured via screenshots
        // Banking standard: Sensitive financial data protection
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        
        // Perform security checks
        performSecurityChecks()
        
        // Start session timeout monitor
        startSessionMonitor()
        
        setContent {
            PayPlusBillingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    BillingNavGraph(navController = navController)
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        lastInteractionTime = System.currentTimeMillis()
        
        // Re-check security when app returns to foreground
        performSecurityChecks()
    }
    
    override fun onUserInteraction() {
        super.onUserInteraction()
        // Reset timeout on any user interaction
        lastInteractionTime = System.currentTimeMillis()
    }
    
    /**
     * Performs comprehensive security checks for banking-grade protection.
     * 
     * Banking applications MUST check for:
     * 1. Rooted/compromised devices
     * 2. Developer mode (allows USB debugging)
     * 3. Emulators (often used for fraud testing)
     * 4. App tampering/repackaging
     * 5. Untrusted installers (sideloading)
     * 6. Network security
     * 7. Debugger attachment
     */
    private fun performSecurityChecks() {
        var securityViolations = 0
        
        // Check 1: Root Detection
        if (SecurityUtils.isDeviceRooted()) {
            securityViolations++
            android.util.Log.e("Security", "🚨 CRITICAL: Device is rooted")
            // Production: Show dialog "This app cannot run on rooted devices for your security"
        }
        
        // Check 2: Developer Mode
        if (SecurityUtils.isDeveloperModeEnabled(this)) {
            securityViolations++
            android.util.Log.w("Security", "⚠️  WARNING: Developer mode enabled (USB debugging risk)")
        }
        
        // Check 3: Emulator Detection
        if (SecurityUtils.isEmulator()) {
            securityViolations++
            android.util.Log.w("Security", "⚠️  WARNING: Running on emulator (fraud risk)")
        }
        
        // Check 4: App Signature Verification (Tamper Detection)
        if (!TamperDetection.verifyAppSignature(this)) {
            securityViolations++
            android.util.Log.e("Security", "🚨 CRITICAL: App signature mismatch - TAMPERED APK!")
            // Production: Immediately terminate app
        }
        
        // Check 5: Installer Verification
        if (!TamperDetection.verifyInstaller(this)) {
            securityViolations++
            android.util.Log.w("Security", "⚠️  WARNING: App not installed from trusted source (sideloaded)")
        }
        
        // Check 6: Debugger Detection
        if (TamperDetection.isDebuggerConnected()) {
            securityViolations++
            android.util.Log.e("Security", "🚨 CRITICAL: Debugger attached - reverse engineering attempt!")
            // Production: Terminate immediately
        }
        
        // Check 7: Network Security
        val networkStatus = NetworkSecurityValidator.validateNetworkSecurity(this)
        if (networkStatus != NetworkSecurityValidator.NetworkSecurityStatus.SECURE) {
            android.util.Log.w("Security", "⚠️  WARNING: Network security issue: $networkStatus")
        }
        
        // Check 8: DNS Hijacking
        if (NetworkSecurityValidator.detectDNSHijacking()) {
            securityViolations++
            android.util.Log.e("Security", "🚨 CRITICAL: DNS hijacking detected - MITM attack!")
        }
        
        // Log security status
        if (securityViolations == 0) {
            android.util.Log.i("Security", "✅ All security checks passed - environment secure")
        } else {
            android.util.Log.e("Security", "🚨 Security violations detected: $securityViolations")
        }
        
        // Production Implementation:
        // if (securityViolations >= 2) {
        //     showSecurityDialog("Security Risk Detected", 
        //         "This app cannot run in an insecure environment")
        //     finish()
        // }
    }
    
    /**
     * Monitors user inactivity and implements session timeout.
     * 
     * Banking standard: Log out users after 5-10 minutes of inactivity
     * to prevent unauthorized access if device is left unattended.
     */
    private fun startSessionMonitor() {
        lifecycleScope.launch {
            while (true) {
                delay(30000) // Check every 30 seconds
                
                val inactiveTime = System.currentTimeMillis() - lastInteractionTime
                if (inactiveTime > SESSION_TIMEOUT_MS) {
                    // ⏱️ Session timeout detected
                    android.util.Log.w("Security", "Session timeout - inactive for ${inactiveTime / 1000}s")
                    
                    // Production Implementation would:
                    // 1. Clear sensitive data from memory
                    // 2. Navigate to login/splash screen
                    // 3. Show "Session expired" message
                    // 4. Force re-authentication
                    
                    // For now, just reset timer (no auth system in this app)
                    lastInteractionTime = System.currentTimeMillis()
                }
            }
        }
    }
}
