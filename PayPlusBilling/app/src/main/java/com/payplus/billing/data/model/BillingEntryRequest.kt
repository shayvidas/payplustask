package com.payplus.billing.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request body for fetching billing entry details or deleting an entry.
 * 
 * @param billingId The unique identifier of the billing entry
 */
data class BillingEntryRequest(
    @SerializedName("billingId")
    val billingId: Long
)
