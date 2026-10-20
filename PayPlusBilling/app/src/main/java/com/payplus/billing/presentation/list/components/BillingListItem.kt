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
 * Matches the Hebrew design with:
 * - Left arrow for navigation
 * - Currency symbol (₪/$) on left
 * - Price and date on right (RTL)
 * - Colored icon strip on far right
 * 
 * @param entry The billing entry header data to display
 * @param onClick Callback invoked when the item is clicked
 */
@Composable
fun BillingListItem(
    entry: BillingEntryHeader,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.White)
            .padding(vertical = 1.dp), // Minimal padding between rows
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LEFT: Back arrow
        Icon(
            painter = painterResource(id = R.drawable.ic_card), // Using available icon as arrow
            contentDescription = "View",
            tint = Color.DarkGray,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // LEFT-CENTER: Currency symbol
        Text(
            text = getCurrencySymbol(entry.currencyCode),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(end = 8.dp)
        )
        
        // CENTER: Spacer to push content to edges
        Spacer(modifier = Modifier.weight(1f))
        
        // RIGHT-CENTER: Price and date (right-aligned)
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            // Price
            Text(
                text = entry.price?.let { String.format("%.2f", it) } ?: "0.00",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            // Entry number and date
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Green checkmark or indicator for some entries
                if ((entry.id ?: 0L) % 3 == 0L) { // Show indicator on every 3rd item
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                
                Text(
                    text = "${entry.entryNumber ?: 0}/${entry.totalEntryCount ?: 0} • ${DateFormatter.formatDate(entry.created)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        
        // FAR RIGHT: Colored icon strip (48dp wide)
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(72.dp)
                .background(getSourceColor(entry.source)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = getSourceIcon(entry.source)),
                contentDescription = entry.source,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
    
    // Thin divider between items
    Divider(
        color = Color.LightGray.copy(alpha = 0.3f),
        thickness = 0.5.dp
    )
}

/**
 * Maps currency code to currency symbol.
 */
private fun getCurrencySymbol(currencyCode: String?): String {
    return when (currencyCode?.uppercase()) {
        Constants.CURRENCY_USD -> Constants.SYMBOL_USD
        Constants.CURRENCY_ILS -> Constants.SYMBOL_ILS
        else -> currencyCode ?: ""
    }
}

/**
 * Maps billing source to corresponding color.
 * Handles null/unknown sources safely.
 */
private fun getSourceColor(source: String?): Color {
    return when (source?.lowercase()) {
        Constants.SOURCE_TERMINAL.lowercase() -> TerminalGreen
        Constants.SOURCE_POS.lowercase() -> PosGreen
        Constants.SOURCE_MANUAL.lowercase() -> ManualGray
        else -> DarkGray
    }
}

/**
 * Maps billing source to corresponding icon resource.
 * Handles null/unknown sources safely.
 */
private fun getSourceIcon(source: String?): Int {
    return when (source?.lowercase()) {
        Constants.SOURCE_TERMINAL.lowercase() -> R.drawable.ic_attendant
        Constants.SOURCE_POS.lowercase() -> R.drawable.ic_pos
        Constants.SOURCE_MANUAL.lowercase() -> R.drawable.ic_card
        else -> R.drawable.ic_card
    }
}

/**
 * Maps currency code to corresponding icon resource.
 * Handles null/unknown currencies safely.
 */
private fun getCurrencyIcon(currencyCode: String?): Int {
    return when (currencyCode?.uppercase()) {
        Constants.CURRENCY_USD -> R.drawable.ic_dollar
        Constants.CURRENCY_ILS -> R.drawable.ic_ils
        else -> R.drawable.ic_dollar
    }
}
