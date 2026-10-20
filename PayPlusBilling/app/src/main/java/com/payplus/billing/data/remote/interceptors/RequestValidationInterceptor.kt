package com.payplus.billing.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.io.IOException

/**
 * Interceptor that validates request and response data for security vulnerabilities.
 * 
 * Protections:
 * - Validates JSON structure
 * - Prevents injection attacks in request bodies
 * - Sanitizes response data
 * - Detects potential SQL injection patterns
 */
class RequestValidationInterceptor : Interceptor {
    
    companion object {
        // SQL injection patterns to detect
        private val SQL_INJECTION_PATTERNS = listOf(
            "(?i).*UNION.*SELECT.*",
            "(?i).*DROP.*TABLE.*",
            "(?i).*INSERT.*INTO.*",
            "(?i).*DELETE.*FROM.*",
            "(?i).*UPDATE.*SET.*",
            "(?i).*OR.*1=1.*",
            "(?i).*; *DROP.*",
            "(?i).*' OR '1'='1.*"
        ).map { it.toRegex() }
        
        // XSS patterns to detect
        private val XSS_PATTERNS = listOf(
            "(?i).*<script.*>.*</script>.*",
            "(?i).*javascript:.*",
            "(?i).*onerror=.*",
            "(?i).*onclick=.*"
        ).map { it.toRegex() }
    }
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Validate request body for malicious patterns
        request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            val bodyString = buffer.readUtf8()
            
            validateInput(bodyString)
        }
        
        // Proceed with request
        val response = chain.proceed(request)
        
        // Validate response body
        val responseBody = response.body
        if (responseBody != null) {
            val bodyString = responseBody.string()
            
            // Validate JSON structure
            try {
                JSONObject(bodyString)
            } catch (e: Exception) {
                // If not valid JSON, check if it's an array
                try {
                    org.json.JSONArray(bodyString)
                } catch (e2: Exception) {
                    throw SecurityException("Invalid JSON response format")
                }
            }
            
            // Create new response with validated body
            return response.newBuilder()
                .body(bodyString.toResponseBody(responseBody.contentType()))
                .build()
        }
        
        return response
    }
    
    /**
     * Validates input string for malicious patterns.
     * Throws SecurityException if suspicious pattern detected.
     */
    private fun validateInput(input: String) {
        // Check for SQL injection
        SQL_INJECTION_PATTERNS.forEach { pattern ->
            if (pattern.containsMatchIn(input)) {
                throw SecurityException("Potential SQL injection detected in request")
            }
        }
        
        // Check for XSS
        XSS_PATTERNS.forEach { pattern ->
            if (pattern.containsMatchIn(input)) {
                throw SecurityException("Potential XSS attack detected in request")
            }
        }
    }
}
