package com.payplus.billing.di

import com.payplus.billing.data.remote.BillingApiService
import com.payplus.billing.data.repository.BillingRepositoryImpl
import com.payplus.billing.domain.repository.BillingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides application-level dependencies.
 * 
 * Binds repository implementations to their interfaces for dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides the BillingRepository implementation.
     * 
     * @param apiService The API service for making network calls
     * @return BillingRepository implementation
     */
    @Provides
    @Singleton
    fun provideBillingRepository(apiService: BillingApiService): BillingRepository {
        return BillingRepositoryImpl(apiService)
    }
}
