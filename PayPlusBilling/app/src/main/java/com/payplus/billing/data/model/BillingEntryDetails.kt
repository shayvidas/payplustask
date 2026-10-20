package com.payplus.billing.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for detailed billing entry information from API.
 * Contains comprehensive details about a single billing transaction.
 * All fields are nullable to handle bad/malformed API data safely.
 */
data class BillingEntryDetails(
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
    
    @SerializedName("currencyCode")
    val currencyCode: String?, // USD, ILS
    
    @SerializedName("amountPaid")
    val amountPaid: Double?,
    
    @SerializedName("status")
    val status: String?, // Passed, Rejected
    
    @SerializedName("cardNumber")
    val cardNumber: String?, // Masked card number
    
    @SerializedName("cardType")
    val cardType: String?, // Visa, MasterCard, Diners, Amex, etc. (can include typos like "Meastro")
    
    @SerializedName("issuer")
    val issuer: String?, // Isracard, Visacal, Diners, Amex, etc.
    
    @SerializedName("source")
    val source: String?, // Terminal, Pos, Manual
    
    @SerializedName("terminalName")
    val terminalName: String?,
    
    @SerializedName("approvalNumber")
    val approvalNumber: String?,
    
    @SerializedName("voucherNumber")
    val voucherNumber: String?
) {
    /**
     * Calculate remaining amount to be paid.
     * Returns 0.0 if price or amountPaid is null.
     */
    fun getRemainingAmount(): Double {
        val p = price ?: 0.0
        val paid = amountPaid ?: 0.0
        return (p - paid).coerceAtLeast(0.0)
    }
}
