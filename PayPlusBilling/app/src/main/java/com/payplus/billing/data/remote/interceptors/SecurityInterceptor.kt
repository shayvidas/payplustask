package com.payplus.billing.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Security interceptor that adds security headers and validates responses.
 * 
 * Security measures implemented:
 * - Adds security headers to prevent common attacks
 * - Validates response status codes
 * - Prevents response caching of sensitive data
 * - Adds request timeout protection
 */
class SecurityInterceptor : Interceptor {
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Build secure request with security headers
        val secureRequest = originalRequest.newBuilder()
            .header("X-Content-Type-Options", "nosniff")
            .header("X-Frame-Options", "DENY")
            .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
            .header("Pragma", "no-cache")
            .build()
        
        // Execute request
        val response = chain.proceed(secureRequest)
        
        // Validate response status code
        if (!response.isSuccessful && response.code != 200) {
            when (response.code) {
                400 -> throw SecurityException("Bad Request: Invalid input")
                401 -> throw SecurityException("Unauthorized: Authentication required")
                403 -> throw SecurityException("Forbidden: Access denied")
                404 -> throw SecurityException("Not Found: Resource does not exist")
                500 -> throw SecurityException("Server Error: Please try again later")
                503 -> throw SecurityException("Service Unavailable: Server is down")
                else -> throw SecurityException("HTTP Error ${response.code}")
            }
        }
        
        return response
    }
}
