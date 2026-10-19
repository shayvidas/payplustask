package com.payplus.billing

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the PayPlus Billing app.
 * 
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 * This triggers Hilt's code generation and sets up the dependency injection container.
 */
@HiltAndroidApp
class BillingApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Application initialization can be added here if needed
    }
}
