package com.payplus.billing.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payplus.billing.R
import com.payplus.billing.presentation.list.components.BillingListItem

/**
 * Billing List Screen with PAGINATION.
 * 
 * Banking Best Practice: Loads 20 items at a time
 * - Better performance (not loading 500 items at once)
 * - Better UX (faster initial load)
 * - "Load More" button to fetch next page
 * 
 * @param onBillingClick Callback invoked when a billing entry is clicked
 * @param viewModel ViewModel with pagination support
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingListScreen(
    onBillingClick: (Long) -> Unit,
    deletedBillingId: Long? = null,
    viewModel: BillingListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Handle deleted item callback from details screen
    LaunchedEffect(deletedBillingId) {
        deletedBillingId?.let { id ->
            if (id > 0) {
                viewModel.onItemDeleted(id)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.billing_list_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is BillingListState.Loading -> {
                    // Loading state - show progress indicator
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is BillingListState.Success -> {
                    // Success state - show list with pagination
                    if (currentState.billingEntries.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_data),
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // List without card padding - flat design
                            LazyColumn(
                                modifier = Modifier.weight(1f)
                            ) {
                                items(
                                    items = currentState.billingEntries,
                                    key = { it.id ?: 0L }
                                ) { entry ->
                                    BillingListItem(
                                        entry = entry,
                                        onClick = { onBillingClick(entry.id ?: 0L) }
                                    )
                                }
                                
                                // Load more button
                                if (viewModel.hasMore()) {
                                    item {
                                        Button(
                                            onClick = { viewModel.loadNextPage() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Text("Load More (${viewModel.getTotalCount() - viewModel.getLoadedCount()} remaining)")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                is BillingListState.Error -> {
                    // Error state - show error message with retry button
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = currentState.message,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Button(onClick = { viewModel.refresh() }) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                }
            }
        }
    }
}
