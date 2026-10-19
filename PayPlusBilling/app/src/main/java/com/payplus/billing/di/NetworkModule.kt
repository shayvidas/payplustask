package com.payplus.billing.di

import com.payplus.billing.data.remote.BillingApiService
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
 * Hilt module that provides network-related dependencies.
 * 
 * Configures Retrofit, OkHttp, and the API service for dependency injection.
 * Note: Use 10.0.2.2 for Android emulator to access localhost on host machine.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * Base URL for the PayPlus Billing API.
     * 
     * For Android Emulator: Use 10.0.2.2 (maps to host machine's localhost)
     * For Physical Device: Use your computer's local IP (e.g., 192.168.1.100)
     */
    private const val BASE_URL = "http://10.0.2.2:8030/"
    
    /**
     * Provides a configured OkHttpClient with logging interceptor.
     * 
     * @return Configured OkHttpClient instance
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
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
