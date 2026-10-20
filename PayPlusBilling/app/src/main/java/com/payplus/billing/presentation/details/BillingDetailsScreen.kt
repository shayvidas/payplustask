package com.payplus.billing.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payplus.billing.R
import com.payplus.billing.data.model.BillingEntryDetails
import com.payplus.billing.presentation.theme.*
import com.payplus.billing.util.Constants
import com.payplus.billing.util.DateFormatter
import com.payplus.billing.util.SecurityUtils

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
    onNavigateBack: (wasDeleted: Boolean) -> Unit,
    viewModel: BillingDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Handle deleted state - navigate back with deleted=true flag
    LaunchedEffect(state) {
        if (state is BillingDetailsState.Deleted) {
            onNavigateBack(true) // ← Notify deletion
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.billing_details_title)) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack(false) }) { // ← Not deleted
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
        // Header with green card icon on right and price on left (RTL)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Price with currency on left (RTL)
            Column(
                horizontalAlignment = Alignment.End // Right-aligned for Hebrew
            ) {
                Text(
                    text = "${getCurrencySymbol(details.currencyCode)}${details.price?.let { String.format("%.2f", it) } ?: "0.00"}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50) // Green like screenshot
                )
                // Entry number and date below price - BOLD
                Text(
                    text = "${details.entryNumber ?: 0}/${details.totalEntryCount ?: 0} • ${DateFormatter.formatDate(details.created)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, // ← MADE BOLD
                    color = Color.Gray
                )
            }
            
            // Green card icon box on right (RTL)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = Color(0xFF4CAF50),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_card),
                    contentDescription = "Card",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Customer name subtitle (in Hebrew) - RIGHT ALIGNED
        Text(
            text = details.issuer ?: "N/A",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End // ← RIGHT ALIGNED
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // All detail rows in Hebrew with RTL layout
        // STATUS with color coding - ONLY the value text
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Status value with color
            val statusText = when(details.status?.lowercase()) {
                "passed" -> "אושר"
                "rejected" -> "נדחה"
                else -> details.status ?: "לא ידוע"
            }
            val statusColor = when(details.status?.lowercase()) {
                "passed" -> Color(0xFF4CAF50)  // Green
                "rejected" -> Color(0xFFE53935) // Red
                else -> Color.Gray
            }
            
            Text(
                text = statusText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor, // ← Only this text is colored
                textAlign = TextAlign.Start
            )
            
            // Label (normal gray)
            Text(
                text = "סטטוס:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.DarkGray,
                textAlign = TextAlign.End
            )
        }
        
        DetailRowHebrew(
            label = "סוג תשלום:",
            value = "זיכוי"
        )
        
        DetailRowHebrew(
            label = "מס. כרטיס:",
            value = SecurityUtils.maskCardNumber(details.cardNumber)
        )
        
        DetailRowHebrew(
            label = "סוג כרטיס:",
            value = details.cardType ?: "לא ידוע"
        )
        
        DetailRowHebrew(
            label = "חברת סליקה:",
            value = details.issuer ?: "לא ידוע"
        )
        
        DetailRowHebrew(
            label = "מקור:",
            value = when(details.source?.lowercase()) {
                "terminal" -> "מסוף"
                "pos" -> "POS"
                "manual" -> "ידני"
                else -> details.source ?: "לא ידוע"
            }
        )
        
        DetailRowHebrew(
            label = "שם מסוף:",
            value = details.terminalName ?: "N/A"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        DetailRowHebrew(
            label = "סכום שחוייב:",
            value = "${getCurrencySymbol(details.currencyCode)}${details.amountPaid?.let { String.format("%.2f", it) } ?: "0.00"}"
        )
        
        val remaining = details.getRemainingAmount()
        DetailRowHebrew(
            label = "סכום נותר לחיוב:",
            value = "${getCurrencySymbol(details.currencyCode)}${String.format("%.2f", remaining)}"
        )
        
        DetailRowHebrew(
            label = "מס. אישור:",
            value = details.approvalNumber ?: "N/A"
        )
        
        DetailRowHebrew(
            label = "מס. רשומה:",
            value = details.voucherNumber ?: "N/A"
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Delete Button with TRASH icon (centered)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = onDeleteClick,
                containerColor = Color(0xFFE91E63), // Pink like screenshot
                modifier = Modifier.size(64.dp)
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    // TRASH CAN ICON - using Material Icons
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "מחק",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

/**
 * Hebrew RTL detail row - label on right, value on left
 */
@Composable
private fun DetailRowHebrew(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Value on left (lighter color)
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Start
        )
        // Label on right (darker, for Hebrew RTL)
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.DarkGray,
            textAlign = TextAlign.End
        )
    }
}

// Helper functions
private fun getSourceColor(source: String?): Color {
    return when (source?.lowercase()) {
        Constants.SOURCE_TERMINAL.lowercase() -> TerminalGreen
        Constants.SOURCE_POS.lowercase() -> PosGreen
        Constants.SOURCE_MANUAL.lowercase() -> ManualGray
        else -> DarkGray
    }
}

private fun getSourceIcon(source: String?): Int {
    return when (source?.lowercase()) {
        Constants.SOURCE_TERMINAL.lowercase() -> R.drawable.ic_attendant
        Constants.SOURCE_POS.lowercase() -> R.drawable.ic_pos
        Constants.SOURCE_MANUAL.lowercase() -> R.drawable.ic_card
        else -> R.drawable.ic_card
    }
}

private fun getStatusColor(status: String?): Color {
    return when (status?.lowercase()) {
        Constants.STATUS_PASSED.lowercase() -> StatusPassed
        Constants.STATUS_REJECTED.lowercase() -> StatusRejected
        else -> Color.Black
    }
}

private fun getCurrencySymbol(currencyCode: String?): String {
    return when (currencyCode?.uppercase()) {
        Constants.CURRENCY_USD -> Constants.SYMBOL_USD
        Constants.CURRENCY_ILS -> Constants.SYMBOL_ILS
        else -> currencyCode ?: ""
    }
}
