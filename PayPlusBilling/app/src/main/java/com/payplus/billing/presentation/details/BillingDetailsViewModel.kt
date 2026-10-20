package com.payplus.billing.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payplus.billing.data.model.BillingEntryDetails
import com.payplus.billing.domain.repository.BillingRepository
import com.payplus.billing.security.FraudDetection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Billing Details with FRAUD DETECTION.
 * 
 * Banking Security: Monitors delete operations for suspicious behavior.
 */
@HiltViewModel
class BillingDetailsViewModel @Inject constructor(
    private val repository: BillingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val billingId: Long = savedStateHandle.get<Long>("billingId") ?: 0L
    
    private val _state = MutableStateFlow<BillingDetailsState>(BillingDetailsState.Loading)
    val state: StateFlow<BillingDetailsState> = _state.asStateFlow()
    
    init {
        loadBillingDetails()
    }
    
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
     * Deletes billing entry with FRAUD DETECTION.
     * 
     * Banking Security:
     * - Detects rapid delete actions (bot behavior)
     * - Logs suspicious activity
     */
    fun deleteBillingEntry() {
        viewModelScope.launch {
            // 🔒 FRAUD DETECTION
            if (FraudDetection.detectRapidActions("delete")) {
                android.util.Log.w("Security", "⚠️  FRAUD ALERT: Rapid delete actions detected!")
            }
            
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
                        exception.message ?: "Failed to delete"
                    )
                }
        }
    }
    
    fun refresh() {
        loadBillingDetails()
    }
}

sealed class BillingDetailsState {
    data object Loading : BillingDetailsState()
    data class Success(
        val details: BillingEntryDetails,
        val isDeleting: Boolean = false
    ) : BillingDetailsState()
    data class Error(val message: String) : BillingDetailsState()
    data object Deleted : BillingDetailsState()
}
