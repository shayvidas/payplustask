package com.payplus.billing.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Rate limiting interceptor to prevent API abuse and DDoS attacks.
 * 
 * Implements token bucket algorithm:
 * - Maximum 10 requests per 10 seconds
 * - Prevents rapid-fire API calls
 * - Protects against accidental infinite loops
 */
class RateLimitInterceptor : Interceptor {
    
    companion object {
        private const val MAX_REQUESTS = 10
        private const val TIME_WINDOW_MS = 10_000L // 10 seconds
    }
    
    private val requestCounts = ConcurrentHashMap<String, RequestWindow>()
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val endpoint = request.url.encodedPath
        
        // Get or create request window for this endpoint
        val window = requestCounts.getOrPut(endpoint) {
            RequestWindow(System.currentTimeMillis())
        }
        
        synchronized(window) {
            val currentTime = System.currentTimeMillis()
            
            // Reset window if time expired
            if (currentTime - window.startTime > TIME_WINDOW_MS) {
                window.startTime = currentTime
                window.count.set(0)
            }
            
            // Check rate limit
            val currentCount = window.count.incrementAndGet()
            if (currentCount > MAX_REQUESTS) {
                throw SecurityException(
                    "Rate limit exceeded: Maximum $MAX_REQUESTS requests per ${TIME_WINDOW_MS / 1000} seconds"
                )
            }
        }
        
        return chain.proceed(request)
    }
    
    /**
     * Data class to track request counts in a time window.
     */
    private data class RequestWindow(
        var startTime: Long,
        val count: AtomicInteger = AtomicInteger(0)
    )
}
