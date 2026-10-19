package com.payplus.billing.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
 * Billing List Screen composable.
 * 
 * Displays a list of billing entries with loading, success, and error states.
 * Uses MVVM pattern with ViewModel and StateFlow for state management.
 * 
 * @param onBillingClick Callback invoked when a billing entry is clicked, passing the billing ID
 * @param viewModel ViewModel for managing the screen state (injected by Hilt)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingListScreen(
    onBillingClick: (Long) -> Unit,
    viewModel: BillingListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
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
                    // Success state - show list of billing entries
                    if (currentState.billingEntries.isEmpty()) {
                        // Empty state
                        Text(
                            text = stringResource(R.string.no_data),
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(
                                items = currentState.billingEntries,
                                key = { it.id }
                            ) { entry ->
                                BillingListItem(
                                    entry = entry,
                                    onClick = { onBillingClick(entry.id) }
                                )
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
