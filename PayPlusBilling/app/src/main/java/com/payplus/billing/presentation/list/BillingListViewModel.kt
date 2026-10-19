package com.payplus.billing.presentation.list

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
 * ViewModel for the Billing List screen.
 * 
 * Manages the UI state and business logic for displaying the list of billing entries.
 * Uses MVVM architecture pattern with StateFlow for reactive UI updates.
 * 
 * @param repository Repository for fetching billing data
 */
@HiltViewModel
class BillingListViewModel @Inject constructor(
    private val repository: BillingRepository
) : ViewModel() {
    
    // Private mutable state
    private val _state = MutableStateFlow<BillingListState>(BillingListState.Loading)
    
    // Public immutable state exposed to UI
    val state: StateFlow<BillingListState> = _state.asStateFlow()
    
    init {
        // Load billing entries when ViewModel is created
        loadBillingEntries()
    }
    
    /**
     * Loads billing entries from the repository.
     * 
     * Updates the state to Loading -> Success or Error based on the result.
     * Executes in a coroutine scope tied to the ViewModel lifecycle.
     */
    fun loadBillingEntries() {
        viewModelScope.launch {
            _state.value = BillingListState.Loading
            
            repository.getBillingHeaders()
                .onSuccess { headers ->
                    _state.value = BillingListState.Success(headers)
                }
                .onFailure { exception ->
                    _state.value = BillingListState.Error(
                        exception.message ?: "Failed to load billing entries"
                    )
                }
        }
    }
    
    /**
     * Refreshes the billing list.
     * Called when user initiates a manual refresh.
     */
    fun refresh() {
        loadBillingEntries()
    }
}
