package com.payplus.billing.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payplus.billing.R
import com.payplus.billing.data.model.BillingEntryDetails
import com.payplus.billing.presentation.theme.*
import com.payplus.billing.util.Constants
import com.payplus.billing.util.DateFormatter

/**
 * Billing Details Screen composable.
 * 
 * Displays detailed information about a billing entry with delete functionality.
 * 
 * @param onNavigateBack Callback invoked when back button is pressed
 * @param viewModel ViewModel for managing the screen state (injected by Hilt)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingDetailsScreen(
    onNavigateBack: () -> Unit,
    viewModel: BillingDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Handle deleted state - navigate back
    LaunchedEffect(state) {
        if (state is BillingDetailsState.Deleted) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.billing_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                is BillingDetailsState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is BillingDetailsState.Success -> {
                    BillingDetailsContent(
                        details = currentState.details,
                        isDeleting = currentState.isDeleting,
                        onDeleteClick = { showDeleteDialog = true }
                    )
                }
                
                is BillingDetailsState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = currentState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.refresh() }) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                }
                
                is BillingDetailsState.Deleted -> {
                    // This state triggers navigation back via LaunchedEffect
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.delete)) },
            text = { Text(text = stringResource(R.string.delete_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteBillingEntry()
                    }
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

/**
 * Content composable for displaying billing details.
 */
@Composable
private fun BillingDetailsContent(
    details: BillingEntryDetails,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header: Source icon and basic info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Source indicator
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(getSourceColor(details.source)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = getSourceIcon(details.source)),
                    contentDescription = details.source,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            // Price with status-based color
            Text(
                text = String.format("%.2f", details.price),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = getStatusColor(details.status)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Date and Entry Number
        DetailRow(
            label = "Date & Time",
            value = DateFormatter.formatTimestamp(details.created)
        )
        
        DetailRow(
            label = stringResource(R.string.entry_number, details.entryNumber),
            value = "Entry #${details.entryNumber} of ${details.totalEntryCount}"
        )
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        // Billing Information
        Text(
            text = "Billing Information",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        DetailRow(
            label = stringResource(R.string.billing_status),
            value = details.status
        )
        
        DetailRow(
            label = stringResource(R.string.payment_type),
            value = "Card Payment"
        )
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        // Card Information
        Text(
            text = "Card Information",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        DetailRow(
            label = stringResource(R.string.card_number),
            value = details.cardNumber
        )
        
        DetailRow(
            label = stringResource(R.string.card_type),
            value = details.cardType
        )
        
        DetailRow(
            label = stringResource(R.string.issuer),
            value = details.issuer
        )
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        // Payment Information
        Text(
            text = "Payment Information",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        DetailRow(
            label = stringResource(R.string.payment_source),
            value = details.source
        )
        
        DetailRow(
            label = stringResource(R.string.terminal_name),
            value = details.terminalName
        )
        
        DetailRow(
            label = stringResource(R.string.total_paid),
            value = "${getCurrencySymbol(details.currencyCode)}${String.format("%.2f", details.amountPaid)}"
        )
        
        val remaining = details.price - details.amountPaid
        DetailRow(
            label = stringResource(R.string.remaining),
            value = "${getCurrencySymbol(details.currencyCode)}${String.format("%.2f", remaining)}"
        )
        
        DetailRow(
            label = stringResource(R.string.approval_number),
            value = details.approvalNumber
        )
        
        DetailRow(
            label = stringResource(R.string.voucher_number),
            value = details.voucherNumber
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Delete Button
        Button(
            onClick = onDeleteClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            enabled = !isDeleting
        ) {
            if (isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onError
                )
            } else {
                Text(text = stringResource(R.string.delete))
            }
        }
    }
}

/**
 * Reusable composable for displaying a label-value pair.
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

// Helper functions
private fun getSourceColor(source: String): Color {
    return when (source.lowercase()) {
        Constants.SOURCE_TERMINAL.lowercase() -> TerminalGreen
        Constants.SOURCE_POS.lowercase() -> PosGreen
        Constants.SOURCE_MANUAL.lowercase() -> ManualGray
        else -> DarkGray
    }
}

private fun getSourceIcon(source: String): Int {
    return when (source.lowercase()) {
        Constants.SOURCE_TERMINAL.lowercase() -> R.drawable.ic_attendant
        Constants.SOURCE_POS.lowercase() -> R.drawable.ic_pos
        Constants.SOURCE_MANUAL.lowercase() -> R.drawable.ic_card
        else -> R.drawable.ic_card
    }
}

private fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        Constants.STATUS_PASSED.lowercase() -> StatusPassed
        Constants.STATUS_REJECTED.lowercase() -> StatusRejected
        else -> Color.Black
    }
}

private fun getCurrencySymbol(currencyCode: String): String {
    return when (currencyCode.uppercase()) {
        Constants.CURRENCY_USD -> Constants.SYMBOL_USD
        Constants.CURRENCY_ILS -> Constants.SYMBOL_ILS
        else -> currencyCode
    }
}
