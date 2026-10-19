package com.payplus.billing.data.model

import com.google.gson.annotations.SerializedName

/**
 * API response for delete billing entry endpoint.
 * 
 * @param status 0 indicates success, non-zero indicates failure
 */
data class DeleteBillingEntryResponse(
    @SerializedName("status")
    val status: Int
)
