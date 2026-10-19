package com.payplus.billing.presentation.list

import com.payplus.billing.data.model.BillingEntryHeader

/**
 * UI state for the Billing List screen.
 * 
 * Represents the different states the list screen can be in:
 * - Loading: Initial state while fetching data
 * - Success: Data loaded successfully
 * - Error: Failed to load data
 */
sealed class BillingListState {
    
    /**
     * Loading state - showing progress indicator
     */
    object Loading : BillingListState()
    
    /**
     * Success state - data loaded and ready to display
     * 
     * @param billingEntries List of billing entry headers to display
     */
    data class Success(val billingEntries: List<BillingEntryHeader>) : BillingListState()
    
    /**
     * Error state - failed to load data
     * 
     * @param message Error message to display to the user
     */
    data class Error(val message: String) : BillingListState()
}
