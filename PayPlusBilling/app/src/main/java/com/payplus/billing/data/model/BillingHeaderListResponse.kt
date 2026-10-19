package com.payplus.billing.data.model

import com.google.gson.annotations.SerializedName

/**
 * API response wrapper for billing headers list endpoint.
 */
data class BillingHeaderListResponse(
    @SerializedName("headers")
    val headers: List<BillingEntryHeader>
)
