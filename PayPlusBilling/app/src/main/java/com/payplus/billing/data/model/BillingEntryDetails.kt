package com.payplus.billing.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for detailed billing entry information from API.
 * Contains comprehensive details about a single billing transaction.
 */
data class BillingEntryDetails(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("created")
    val created: Long, // Unix timestamp in milliseconds
    
    @SerializedName("entryNumber")
    val entryNumber: Int,
    
    @SerializedName("totalEntryCount")
    val totalEntryCount: Int,
    
    @SerializedName("currencyCode")
    val currencyCode: String, // USD, ILS
    
    @SerializedName("amountPaid")
    val amountPaid: Double,
    
    @SerializedName("status")
    val status: String, // Passed, Rejected
    
    @SerializedName("cardNumber")
    val cardNumber: String, // Masked card number
    
    @SerializedName("cardType")
    val cardType: String, // Visa, MasterCard, Diners, Amex, etc.
    
    @SerializedName("issuer")
    val issuer: String, // Isracard, Visacal, Diners, Amex, etc.
    
    @SerializedName("source")
    val source: String, // Terminal, Pos, Manual
    
    @SerializedName("terminalName")
    val terminalName: String,
    
    @SerializedName("approvalNumber")
    val approvalNumber: String,
    
    @SerializedName("voucherNumber")
    val voucherNumber: String
)
