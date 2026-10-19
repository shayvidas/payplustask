package com.payplus.billing.domain.repository

import com.payplus.billing.data.model.BillingEntryDetails
import com.payplus.billing.data.model.BillingEntryHeader

/**
 * Repository interface defining data operations for billing entries.
 * 
 * This abstraction allows the presentation layer to be independent of the data source,
 * following the Repository pattern and enabling easier testing.
 */
interface BillingRepository {
    
    /**
     * Fetches all billing entry headers.
     * 
     * @return Result containing list of billing headers or error
     */
    suspend fun getBillingHeaders(): Result<List<BillingEntryHeader>>
    
    /**
     * Fetches detailed information for a specific billing entry.
     * 
     * @param billingId The unique identifier of the billing entry
     * @return Result containing billing details or error
     */
    suspend fun getBillingDetails(billingId: Long): Result<BillingEntryDetails>
    
    /**
     * Deletes a specific billing entry.
     * 
     * @param billingId The unique identifier of the billing entry to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteBillingEntry(billingId: Long): Result<Unit>
}
