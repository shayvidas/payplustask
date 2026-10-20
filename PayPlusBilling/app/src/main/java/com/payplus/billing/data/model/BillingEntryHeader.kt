package com.payplus.billing.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for billing entry header from API.
 * Represents a single item in the billing list.
 * All fields are nullable to handle bad/malformed API data safely.
 */
data class BillingEntryHeader(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("price")
    val price: Double?,
    
    @SerializedName("created")
    val created: Long?, // Unix timestamp in SECONDS (not milliseconds!)
    
    @SerializedName("entryNumber")
    val entryNumber: Int?,
    
    @SerializedName("totalEntryCount")
    val totalEntryCount: Int?,
    
    @SerializedName("source")
    val source: String?, // Terminal, Pos, Manual
    
    @SerializedName("currencyCode")
    val currencyCode: String?, // USD, ILS
    
    @SerializedName("cardType")
    val cardType: String? // Visa, MasterCard, etc. (can include typos like "Meastro")
)
