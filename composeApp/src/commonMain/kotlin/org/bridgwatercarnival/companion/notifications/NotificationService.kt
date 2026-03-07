package org.bridgwatercarnival.companion.notifications

import androidx.compose.runtime.Composable

interface NotificationService {
    fun requestNotificationPermission()
    fun scheduleDailyCountdownNotification()
    fun cancelDailyCountdownNotification()
    fun isNotificationPermissionGranted(): Boolean
    fun showTestNotification()
    fun openNotificationSettings()
    
    // Commented out voting and results notifications
    // fun scheduleVotingNotifications()
    // fun cancelVotingNotifications()
    // fun scheduleResultsNotifications()
    // fun cancelResultsNotifications()
}

@Composable
expect fun getNotificationService(): NotificationService 