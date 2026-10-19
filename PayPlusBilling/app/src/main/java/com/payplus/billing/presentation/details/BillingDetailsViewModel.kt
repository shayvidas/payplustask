package com.payplus.billing.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payplus.billing.domain.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Billing Details screen.
 * 
 * Manages the UI state and business logic for displaying and deleting billing entry details.
 * Uses MVVM architecture pattern with StateFlow for reactive UI updates.
 * 
 * @param repository Repository for fetching and deleting billing data
 * @param savedStateHandle Saved state handle for retrieving navigation arguments
 */
@HiltViewModel
class BillingDetailsViewModel @Inject constructor(
    private val repository: BillingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // Private mutable state
    private val _state = MutableStateFlow<BillingDetailsState>(BillingDetailsState.Loading)
    
    // Public immutable state exposed to UI
    val state: StateFlow<BillingDetailsState> = _state.asStateFlow()
    
    // Billing ID from navigation arguments
    private val billingId: Long = savedStateHandle.get<Long>("billingId") ?: 0L
    
    init {
        // Load billing details when ViewModel is created
        loadBillingDetails()
    }
    
    /**
     * Loads billing entry details from the repository.
     * 
     * Updates the state to Loading -> Success or Error based on the result.
     */
    private fun loadBillingDetails() {
        viewModelScope.launch {
            _state.value = BillingDetailsState.Loading
            
            repository.getBillingDetails(billingId)
                .onSuccess { details ->
                    _state.value = BillingDetailsState.Success(details)
                }
                .onFailure { exception ->
                    _state.value = BillingDetailsState.Error(
                        exception.message ?: "Failed to load billing details"
                    )
                }
        }
    }
    
    /**
     * Deletes the current billing entry.
     * 
     * Shows loading state during deletion, then transitions to Deleted state on success
     * or Error state on failure.
     */
    fun deleteBillingEntry() {
        viewModelScope.launch {
            // Set deleting flag to show progress indicator
            val currentState = _state.value
            if (currentState is BillingDetailsState.Success) {
                _state.value = currentState.copy(isDeleting = true)
            }
            
            repository.deleteBillingEntry(billingId)
                .onSuccess {
                    _state.value = BillingDetailsState.Deleted
                }
                .onFailure { exception ->
                    _state.value = BillingDetailsState.Error(
                        exception.message ?: "Failed to delete billing entry"
                    )
                }
        }
    }
    
    /**
     * Refreshes the billing details.
     * Called when user requests a retry after an error.
     */
    fun refresh() {
        loadBillingDetails()
    }
}
