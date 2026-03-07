package org.bridgwatercarnival.companion.notifications

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
 
@Composable
actual fun getNotificationService(): NotificationService {
    return AndroidNotificationService(LocalContext.current)
} 