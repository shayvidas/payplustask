package com.payplus.billing.data.repository

import com.payplus.billing.data.model.BillingEntryDetails
import com.payplus.billing.data.model.BillingEntryHeader
import com.payplus.billing.data.model.BillingEntryRequest
import com.payplus.billing.data.model.BillingListRequest
import com.payplus.billing.data.remote.BillingApiService
import com.payplus.billing.domain.repository.BillingRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [BillingRepository] that communicates with the remote API.
 * 
 * Handles all data operations and error handling, providing a clean interface
 * to the presentation layer. Uses Kotlin's Result type for error handling.
 * 
 * @param apiService The Retrofit service for API calls
 */
@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val apiService: BillingApiService
) : BillingRepository {
    
    /**
     * Fetches all billing entry headers from the API.
     * 
     * @return Result.success with list of headers, or Result.failure on error
     */
    override suspend fun getBillingHeaders(): Result<List<BillingEntryHeader>> {
        return try {
            val response = apiService.getBillingHeaders(BillingListRequest())
            Result.success(response.headers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetches detailed information for a specific billing entry.
     * 
     * @param billingId The unique identifier of the billing entry
     * @return Result.success with details, or Result.failure on error
     */
    override suspend fun getBillingDetails(billingId: Long): Result<BillingEntryDetails> {
        return try {
            val response = apiService.getBillingDetails(BillingEntryRequest(billingId))
            response.details?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Billing details not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Deletes a specific billing entry via the API.
     * 
     * @param billingId The unique identifier of the billing entry to delete
     * @return Result.success if status == 0, Result.failure otherwise
     */
    override suspend fun deleteBillingEntry(billingId: Long): Result<Unit> {
        return try {
            val response = apiService.deleteBillingEntry(BillingEntryRequest(billingId))
            if (response.status == 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete billing entry. Status: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
