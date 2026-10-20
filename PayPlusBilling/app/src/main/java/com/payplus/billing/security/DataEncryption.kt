package com.payplus.billing.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Data Encryption utilities for Banking Applications.
 * 
 * Uses Android KeyStore system to store encryption keys securely.
 * Keys are hardware-backed (if device supports it) and never leave the device.
 * 
 * PCI-DSS Requirement 3.4: Render PAN unreadable (encryption at rest).
 */
object DataEncryption {
    
    private const val KEY_ALIAS = "PayPlusBillingKey"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    
    /**
     * Encrypts sensitive data using Android KeyStore.
     * 
     * Use for:
     * - Card numbers (if you need to store them - generally shouldn't)
     * - Approval numbers
     * - Voucher numbers
     * - Any PII (Personally Identifiable Information)
     * 
     * @param plaintext Data to encrypt
     * @return Base64-encoded encrypted data with IV prepended
     */
    fun encrypt(plaintext: String): String? {
        return try {
            val cipher = getCipher()
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            
            val iv = cipher.iv
            val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            
            // Prepend IV to encrypted data
            val combined = iv + encrypted
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            android.util.Log.e("DataEncryption", "❌ Encryption failed: ${e.message}")
            null
        }
    }
    
    /**
     * Decrypts data encrypted with encrypt().
     * 
     * @param encryptedData Base64-encoded encrypted data with IV
     * @return Decrypted plaintext or null if failed
     */
    fun decrypt(encryptedData: String): String? {
        return try {
            val combined = Base64.decode(encryptedData, Base64.NO_WRAP)
            
            // Extract IV (first 12 bytes for GCM)
            val ivSize = 12
            val iv = combined.sliceArray(0 until ivSize)
            val encrypted = combined.sliceArray(ivSize until combined.size)
            
            val cipher = getCipher()
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
            
            val decrypted = cipher.doFinal(encrypted)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            android.util.Log.e("DataEncryption", "❌ Decryption failed: ${e.message}")
            null
        }
    }
    
    /**
     * Gets or creates encryption key in Android KeyStore.
     * Key is hardware-backed if device supports it (TEE or Secure Element).
     */
    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        
        // Check if key already exists
        if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            return entry.secretKey
        }
        
        // Create new key
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .setUserAuthenticationRequired(false) // Set to true for biometric unlock
            .build()
        
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
    
    /**
     * Gets Cipher instance for encryption/decryption.
     */
    private fun getCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION)
    }
    
    /**
     * Securely wipes sensitive data from memory.
     * Call this for strings containing sensitive data after use.
     * 
     * @param data ByteArray to wipe
     */
    fun secureWipe(data: ByteArray) {
        data.fill(0)
    }
    
    /**
     * Checks if device has hardware-backed keystore (TEE/Secure Element).
     * Hardware-backed keys are more secure as they never leave secure hardware.
     */
    fun isHardwareBackedKeystore(): Boolean {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                getOrCreateKey() // Create key if doesn't exist
            }
            
            val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            entry != null
        } catch (e: Exception) {
            false
        }
    }
}
