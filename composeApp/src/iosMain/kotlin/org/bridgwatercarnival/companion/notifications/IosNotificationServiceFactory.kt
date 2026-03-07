package org.bridgwatercarnival.companion.notifications

import androidx.compose.runtime.Composable
 
@Composable
actual fun getNotificationService(): NotificationService {
    return IosNotificationService()
} 