package com.payplus.billing.data.remote.interceptors

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

/**
 * Certificate Pinning configuration for MITM attack prevention.
 * 
 * Certificate pinning ensures the app only trusts specific SSL certificates,
 * preventing Man-in-the-Middle (MITM) attacks where attackers intercept traffic.
 * 
 * CRITICAL for banking apps: Prevents SSL stripping and traffic interception.
 * 
 * Production Implementation:
 * 1. Get your production server's certificate pins
 * 2. Add them to the CertificatePinner
 * 3. Include backup pins for rotation
 * 
 * To get certificate pins for your domain:
 * ```bash
 * openssl s_client -connect yourdomain.com:443 | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
 * ```
 */
object CertificatePinningInterceptor {
    
    /**
     * Creates OkHttpClient with certificate pinning enabled.
     * 
     * WARNING: Certificate pinning is disabled for localhost/development.
     * MUST be enabled for production with actual certificate pins.
     * 
     * @return OkHttpClient with certificate pinning
     */
    fun createPinnedClient(baseClient: OkHttpClient): OkHttpClient {
        // Development: No pinning for localhost
        // Production: Add your actual certificate pins here
        
        val certificatePinner = CertificatePinner.Builder()
            // Example for production (replace with your actual pins):
            // .add("api.payplus.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
            // .add("api.payplus.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=") // Backup pin
            .build()
        
        return baseClient.newBuilder()
            // .certificatePinner(certificatePinner) // Uncomment for production
            .build()
    }
    
    /**
     * Production certificate pins would look like:
     * 
     * ```kotlin
     * val certificatePinner = CertificatePinner.Builder()
     *     .add("api.payplus.com", "sha256/AAAAAAAAAA...") // Primary certificate
     *     .add("api.payplus.com", "sha256/BBBBBBBBBB...") // Backup certificate
     *     .build()
     * ```
     * 
     * Best practices:
     * 1. Pin at least 2 certificates (primary + backup)
     * 2. Update pins before certificate expiration
     * 3. Have a plan for emergency pin updates
     * 4. Log pin failures to detect attacks
     */
}
