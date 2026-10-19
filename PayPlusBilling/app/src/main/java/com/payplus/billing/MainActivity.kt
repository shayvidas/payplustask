package com.payplus.billing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.payplus.billing.presentation.navigation.BillingNavGraph
import com.payplus.billing.presentation.theme.PayPlusBillingTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the PayPlus Billing application.
 * 
 * Entry point of the app using Jetpack Compose for UI.
 * Annotated with @AndroidEntryPoint to enable Hilt dependency injection in this activity.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            PayPlusBillingTheme {
                // Surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    BillingNavGraph(navController = navController)
                }
            }
        }
    }
}
