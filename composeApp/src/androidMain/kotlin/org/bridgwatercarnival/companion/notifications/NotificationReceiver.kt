package org.bridgwatercarnival.companion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action == "DAILY_NOTIFICATION") {
            val notificationService = AndroidNotificationService(context)
            val daysUntil = notificationService.calculateDaysUntilNovemberFirst()
            notificationService.showCountdownNotification(daysUntil)
        }
    }
} 