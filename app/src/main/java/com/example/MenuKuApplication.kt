package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuKuApplication : Application() {

    companion object {
        const val ONESIGNAL_APP_ID = "89574a92-4428-4a46-9eaa-0f180e8d680d"

        fun requestNotificationPermission(fallbackToSettings: Boolean = true) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    OneSignal.Notifications.requestPermission(fallbackToSettings)
                    OneSignal.User.pushSubscription.optIn()
                } catch (e: Exception) {
                    Log.w("MenuKuApplication", "Error requesting OneSignal permission", e)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            // Explicitly initialize Firebase first
            FirebaseApp.initializeApp(this)

            // OneSignal Debug Log Level
            OneSignal.Debug.logLevel = LogLevel.WARN

            // OneSignal Initialization with user's App ID
            OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

            // Request push notification permission in the background and opt-in
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    OneSignal.Notifications.requestPermission(false)
                    OneSignal.User.pushSubscription.optIn()
                } catch (e: Exception) {
                    Log.w("MenuKuApplication", "OneSignal permission initial check", e)
                }
            }

            Log.i("MenuKuApplication", "OneSignal initialized successfully with ID: $ONESIGNAL_APP_ID")
        } catch (e: Throwable) {
            Log.e("MenuKuApplication", "Failed to initialize OneSignal", e)
        }
    }
}
