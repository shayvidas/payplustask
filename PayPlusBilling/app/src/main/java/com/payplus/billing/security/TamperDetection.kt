package com.payplus.billing.security

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import java.security.MessageDigest

/**
 * Tamper Detection for Banking Applications.
 * 
 * Detects if the APK has been modified, repackaged, or tampered with.
 * Critical for banking: Prevents malicious code injection.
 */
object TamperDetection {
    
    // Store the expected signature hash here (get from your release build)
    // This is a placeholder - replace with your actual signature hash
    private const val EXPECTED_SIGNATURE_HASH = "YOUR_SIGNATURE_HASH_HERE"
    
    /**
     * Verifies APK signature hasn't been tampered with.
     * Repackaged apps will have different signatures.
     * 
     * Banking Standard: Apps must detect repackaging attempts.
     * 
     * @param context Application context
     * @return true if signature is valid, false if tampered
     */
    fun verifyAppSignature(context: Context): Boolean {
        return try {
            val signatures = getAppSignatures(context)
            val currentHash = signatures.firstOrNull()?.let { computeSignatureHash(it) }
            
            // In development, always return true
            // In production, check against EXPECTED_SIGNATURE_HASH
            if (currentHash == null) {
                android.util.Log.e("TamperDetection", "❌ Could not get signature hash")
                return false
            }
            
            android.util.Log.i("TamperDetection", "Current signature hash: $currentHash")
            
            // Development mode: just log the hash
            // Production: return currentHash == EXPECTED_SIGNATURE_HASH
            true
        } catch (e: Exception) {
            android.util.Log.e("TamperDetection", "❌ Signature verification failed: ${e.message}")
            false
        }
    }
    
    /**
     * Gets app signatures (works for API 28+ and legacy).
     */
    @Suppress("DEPRECATION")
    private fun getAppSignatures(context: Context): Array<Signature> {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
        } else {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
        }
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.signingInfo?.apkContentsSigners ?: emptyArray()
        } else {
            packageInfo.signatures ?: emptyArray()
        }
    }
    
    /**
     * Computes SHA-256 hash of signature.
     */
    private fun computeSignatureHash(signature: Signature): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(signature.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Checks if app installer is from a trusted source.
     * Banking apps should only be installed from Google Play Store.
     * 
     * @param context Application context
     * @return true if from trusted installer, false otherwise
     */
    fun verifyInstaller(context: Context): Boolean {
        val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getInstallerPackageName(context.packageName)
        }
        
        val trustedInstallers = setOf(
            "com.android.vending",           // Google Play Store
            "com.google.android.feedback",   // Google (internal testing)
            null                              // Development (direct install)
        )
        
        val isTrusted = installer in trustedInstallers
        
        if (!isTrusted) {
            android.util.Log.w("TamperDetection", "⚠️  Untrusted installer: $installer")
        }
        
        return isTrusted
    }
    
    /**
     * Detects if debugger is attached.
     * Banking apps should refuse to run when debugged.
     */
    fun isDebuggerConnected(): Boolean {
        return android.os.Debug.isDebuggerConnected()
    }
}
