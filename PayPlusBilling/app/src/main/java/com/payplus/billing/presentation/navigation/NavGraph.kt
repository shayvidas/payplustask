package com.payplus.billing.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.payplus.billing.presentation.details.BillingDetailsScreen
import com.payplus.billing.presentation.list.BillingListScreen

/**
 * Navigation graph for the PayPlus Billing app.
 * 
 * Defines all navigation routes and composable destinations.
 */
object NavigationRoutes {
    const val BILLING_LIST = "billing_list"
    const val BILLING_DETAILS = "billing_details/{billingId}"
    
    // Result keys for navigation
    const val DELETED_ID_KEY = "deleted_billing_id"
    
    /**
     * Creates the billing details route with the provided billing ID.
     */
    fun billingDetails(billingId: Long) = "billing_details/$billingId"
}

/**
 * Sets up the navigation graph for the app.
 * 
 * @param navController The navigation controller for managing navigation
 */
@Composable
fun BillingNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.BILLING_LIST
    ) {
        // Billing List Screen
        composable(route = NavigationRoutes.BILLING_LIST) { backStackEntry ->
            // Get deleted ID from details screen if returning from delete
            val deletedId = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.get<Long>(NavigationRoutes.DELETED_ID_KEY)
            
            BillingListScreen(
                onBillingClick = { billingId ->
                    navController.navigate(NavigationRoutes.billingDetails(billingId))
                },
                deletedBillingId = deletedId
            )
        }
        
        // Billing Details Screen
        composable(
            route = NavigationRoutes.BILLING_DETAILS,
            arguments = listOf(
                navArgument("billingId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val billingId = backStackEntry.arguments?.getLong("billingId") ?: 0L
            
            BillingDetailsScreen(
                onNavigateBack = { wasDeleted ->
                    if (wasDeleted) {
                        // Pass deleted ID back to list screen
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(NavigationRoutes.DELETED_ID_KEY, billingId)
                    }
                    navController.popBackStack()
                }
            )
        }
    }
}
