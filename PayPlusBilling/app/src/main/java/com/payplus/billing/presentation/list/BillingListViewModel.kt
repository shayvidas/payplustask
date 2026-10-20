package com.payplus.billing.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payplus.billing.domain.repository.BillingRepository
import com.payplus.billing.data.model.BillingEntryHeader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the billing list screen with PAGINATION.
 * 
 * Banking Best Practice: Don't load 500+ entries at once
 * - Page size: 20 items
 * - Load more on scroll
 * - Better performance
 * - Better UX
 */
@HiltViewModel
class BillingListViewModel @Inject constructor(
    private val repository: BillingRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow<BillingListState>(BillingListState.Loading)
    val state: StateFlow<BillingListState> = _state.asStateFlow()
    
    // Pagination state
    private var allEntries: List<BillingEntryHeader> = emptyList()
    private var currentPage = 0
    private val pageSize = 20
    
    init {
        loadBillingHeaders()
    }
    
    /**
     * Loads billing headers from repository.
     */
    fun loadBillingHeaders() {
        viewModelScope.launch {
            _state.value = BillingListState.Loading
            
            repository.getBillingHeaders()
                .onSuccess { entries ->
                    allEntries = entries
                    currentPage = 0
                    loadNextPage()
                }
                .onFailure { error ->
                    _state.value = BillingListState.Error(
                        error.message ?: "Failed to load billing entries"
                    )
                }
        }
    }
    
    /**
     * Loads the next page of items.
     */
    fun loadNextPage() {
        if (allEntries.isEmpty()) return
        
        val startIndex = currentPage * pageSize
        val endIndex = minOf(startIndex + pageSize, allEntries.size)
        
        if (startIndex >= allEntries.size) return
        
        val currentItems = (_state.value as? BillingListState.Success)?.billingEntries ?: emptyList()
        val newItems = allEntries.subList(startIndex, endIndex)
        
        _state.value = BillingListState.Success(
            billingEntries = currentItems + newItems
        )
        
        currentPage++
    }
    
    fun hasMore(): Boolean {
        return (currentPage * pageSize) < allEntries.size
    }
    
    fun getLoadedCount(): Int {
        return minOf(currentPage * pageSize, allEntries.size)
    }
    
    fun getTotalCount(): Int {
        return allEntries.size
    }
    
    /**
     * Resets pagination and reloads.
     */
    fun refresh() {
        currentPage = 0
        loadBillingHeaders()
    }
    
    /**
     * Removes a deleted item from the list immediately (optimistic update).
     * Then refreshes from server to ensure consistency.
     */
    fun onItemDeleted(billingId: Long) {
        // Remove from allEntries
        allEntries = allEntries.filter { it.id != billingId }
        
        // Remove from current displayed items
        val currentState = _state.value
        if (currentState is BillingListState.Success) {
            _state.value = BillingListState.Success(
                billingEntries = currentState.billingEntries.filter { it.id != billingId }
            )
        }
        
        // Refresh from server in background to ensure consistency
        viewModelScope.launch {
            repository.getBillingHeaders()
                .onSuccess { entries ->
                    allEntries = entries
                    // Reload the same number of items we had before
                    val itemsToShow = minOf(currentPage * pageSize, allEntries.size)
                    _state.value = BillingListState.Success(
                        billingEntries = allEntries.take(itemsToShow)
                    )
                }
        }
    }
}

/**
 * Sealed class representing the state of the billing list with pagination info.
 */
sealed class BillingListState {
    data object Loading : BillingListState()
    
    data class Success(
        val billingEntries: List<BillingEntryHeader>
    ) : BillingListState()
    
    data class Error(val message: String) : BillingListState()
}
