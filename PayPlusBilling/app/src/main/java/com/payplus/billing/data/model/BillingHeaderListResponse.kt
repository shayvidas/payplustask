package com.payplus.billing.data.model

import com.google.gson.annotations.SerializedName

/**
 * API response wrapper for billing headers list endpoint.
 * The headers list will be empty if there's an error or no data.
 */
data class BillingHeaderListResponse(
    @SerializedName("headers")
    val headers: List<BillingEntryHeader>?
)
