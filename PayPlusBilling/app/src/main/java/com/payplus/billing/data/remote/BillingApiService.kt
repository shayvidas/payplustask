package com.payplus.billing.data.remote

import com.payplus.billing.data.model.*
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API service interface for billing operations.
 * Defines all network endpoints for the PayPlus Billing API.
 */
interface BillingApiService {
    
    /**
     * Fetches the list of billing entry headers.
     * 
     * @param request Empty request body as per API specification
     * @return Response containing list of billing headers
     */
    @POST("payment/billing/entry/headers")
    suspend fun getBillingHeaders(
        @Body request: BillingListRequest = BillingListRequest()
    ): BillingHeaderListResponse
    
    /**
     * Fetches detailed information for a specific billing entry.
     * 
     * @param request Request containing the billing ID
     * @return Response containing detailed billing information
     */
    @POST("payment/billing/entry/details")
    suspend fun getBillingDetails(
        @Body request: BillingEntryRequest
    ): BillingEntryDetailsResponse
    
    /**
     * Deletes a specific billing entry.
     * 
     * @param request Request containing the billing ID to delete
     * @return Response with status code (0 = success)
     */
    @POST("payment/billing/entry/delete")
    suspend fun deleteBillingEntry(
        @Body request: BillingEntryRequest
    ): DeleteBillingEntryResponse
}
