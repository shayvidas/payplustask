package com.payplus.billing.data.model

import com.google.gson.annotations.SerializedName

/**
 * API response wrapper for billing entry details endpoint.
 */
data class BillingEntryDetailsResponse(
    @SerializedName("details")
    val details: BillingEntryDetails?
)
