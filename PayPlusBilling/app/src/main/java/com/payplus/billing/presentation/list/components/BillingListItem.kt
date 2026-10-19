package com.payplus.billing.presentation.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.payplus.billing.R
import com.payplus.billing.data.model.BillingEntryHeader
import com.payplus.billing.presentation.theme.*
import com.payplus.billing.util.Constants
import com.payplus.billing.util.DateFormatter

/**
 * Composable for displaying a single billing list item.
 * 
 * Shows billing entry information including source icon, price, date,
 * billing number, currency icon, and optional upload button for MasterCard.
 * 
 * @param entry The billing entry header data to display
 * @param onClick Callback invoked when the item is clicked
 */
@Composable
fun BillingListItem(
    entry: BillingEntryHeader,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Source icon with colored background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getSourceColor(entry.source)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = getSourceIcon(entry.source)),
                    contentDescription = entry.source,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Middle: Price, date, and billing number
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Price (without currency symbol)
                Text(
                    text = String.format("%.2f", entry.price),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Date and time
                Text(
                    text = DateFormatter.formatTimestamp(entry.created),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Billing number
                Text(
                    text = "Entry #${entry.entryNumber}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Right side: Upload button (for MasterCard), currency icon, arrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Upload button - only for MasterCard
                if (entry.cardType.equals(Constants.CARD_MASTERCARD, ignoreCase = true)) {
                    IconButton(
                        onClick = { /* Upload action */ },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(LightGray)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Upload",
                            tint = DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Currency icon
                Icon(
                    painter = painterResource(id = getCurrencyIcon(entry.currencyCode)),
                    contentDescription = entry.currencyCode,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                
                // Arrow icon
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Maps billing source to corresponding color.
 */
private fun getSourceColor(source: String): Color {
    return when (source.lowercase()) {
        Constants.SOURCE_TERMINAL.lowercase() -> TerminalGreen
        Constants.SOURCE_POS.lowercase() -> PosGreen
        Constants.SOURCE_MANUAL.lowercase() -> ManualGray
        else -> DarkGray
    }
}

/**
 * Maps billing source to corresponding icon resource.
 */
private fun getSourceIcon(source: String): Int {
    return when (source.lowercase()) {
        Constants.SOURCE_TERMINAL.lowercase() -> R.drawable.ic_attendant
        Constants.SOURCE_POS.lowercase() -> R.drawable.ic_pos
        Constants.SOURCE_MANUAL.lowercase() -> R.drawable.ic_card
        else -> R.drawable.ic_card
    }
}

/**
 * Maps currency code to corresponding icon resource.
 */
private fun getCurrencyIcon(currencyCode: String): Int {
    return when (currencyCode.uppercase()) {
        Constants.CURRENCY_USD -> R.drawable.ic_dollar
        Constants.CURRENCY_ILS -> R.drawable.ic_ils
        else -> R.drawable.ic_dollar
    }
}
