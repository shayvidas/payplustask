package com.payplus.billing.presentation.details

import com.payplus.billing.data.model.BillingEntryDetails

/**
 * UI state for the Billing Details screen.
 * 
 * Represents the different states the details screen can be in.
 */
sealed class BillingDetailsState {
    
    /**
     * Loading state - fetching billing details
     */
    object Loading : BillingDetailsState()
    
    /**
     * Success state - details loaded and ready to display
     * 
     * @param details The billing entry details
     * @param isDeleting Whether a delete operation is in progress
     */
    data class Success(
        val details: BillingEntryDetails,
        val isDeleting: Boolean = false
    ) : BillingDetailsState()
    
    /**
     * Error state - failed to load or process data
     * 
     * @param message Error message to display
     */
    data class Error(val message: String) : BillingDetailsState()
    
    /**
     * Deleted state - entry was successfully deleted
     */
    object Deleted : BillingDetailsState()
}
