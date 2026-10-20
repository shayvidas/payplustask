package com.payplus.billing.di

import com.payplus.billing.BuildConfig
import com.payplus.billing.data.remote.BillingApiService
import com.payplus.billing.data.remote.interceptors.RateLimitInterceptor
import com.payplus.billing.data.remote.interceptors.RequestValidationInterceptor
import com.payplus.billing.data.remote.interceptors.SecurityInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module that provides network-related dependencies with security layers.
 * 
 * Security enhancements:
 * - Rate limiting to prevent API abuse
 * - Request validation to prevent injection attacks
 * - Security headers and response validation
 * - Environment-specific configuration (debug/release)
 * - Proper timeouts to prevent indefinite hangs
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * Base URL for the PayPlus Billing API.
     * 
     * Security: Using BuildConfig for different environments (debug/release)
     * For Android Emulator: Use 10.0.2.2 (maps to host machine's localhost)
     * For Physical Device: Use your computer's local IP (e.g., 192.168.1.100)
     */
    private val BASE_URL get() = BuildConfig.API_BASE_URL
    
    /**
     * Provides a configured OkHttpClient with security interceptors.
     * 
     * Security layers added:
     * 1. Rate limiting to prevent API abuse
     * 2. Request validation to prevent injection attacks
     * 3. Security headers and response validation
     * 4. Logging (only in debug builds)
     * 
     * @return Configured OkHttpClient instance with security layers
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            // Security Layer 1: Rate limiting (prevents DDoS/abuse)
            .addInterceptor(RateLimitInterceptor())
            // Security Layer 2: Input validation (prevents injection)
            .addInterceptor(RequestValidationInterceptor())
            // Security Layer 3: Security headers and response validation
            .addInterceptor(SecurityInterceptor())
            // Timeouts (security: prevent indefinite hangs)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            // Call timeout (overall request timeout)
            .callTimeout(30, TimeUnit.SECONDS)
            // Retry configuration
            .retryOnConnectionFailure(true)
        
        // Add logging only in debug builds (don't leak data in production)
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        
        return builder.build()
    }
    
    /**
     * Provides a configured Retrofit instance.
     * 
     * @param okHttpClient The OkHttpClient to use for network calls
     * @return Configured Retrofit instance
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Provides the BillingApiService implementation.
     * 
     * @param retrofit The Retrofit instance to create the service from
     * @return BillingApiService implementation
     */
    @Provides
    @Singleton
    fun provideBillingApiService(retrofit: Retrofit): BillingApiService {
        return retrofit.create(BillingApiService::class.java)
    }
}
