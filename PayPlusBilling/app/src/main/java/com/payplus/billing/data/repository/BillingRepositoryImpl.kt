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
     * Filters out entries with missing critical fields (id, price, created).
     * 
     * @return Result.success with list of valid headers, or Result.failure on error
     */
    override suspend fun getBillingHeaders(): Result<List<BillingEntryHeader>> {
        return try {
            val response = apiService.getBillingHeaders(BillingListRequest())
            val headers = response.headers ?: emptyList()
            
            // Filter out entries with missing critical fields for safety
            val validHeaders = headers.filter { header ->
                header.id != null && 
                header.price != null && 
                header.created != null &&
                header.entryNumber != null &&
                header.totalEntryCount != null
            }
            
            Result.success(validHeaders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetches detailed information for a specific billing entry.
     * Validates that all critical fields exist before returning.
     * 
     * @param billingId The unique identifier of the billing entry
     * @return Result.success with details, or Result.failure on error/missing data
     */
    override suspend fun getBillingDetails(billingId: Long): Result<BillingEntryDetails> {
        return try {
            val response = apiService.getBillingDetails(BillingEntryRequest(billingId))
            val details = response.details
            
            when {
                details == null -> Result.failure(Exception("Billing details not found"))
                details.id == null -> Result.failure(Exception("Invalid billing entry: missing ID"))
                else -> Result.success(details)
            }
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
