package org.bridgwatercarnival.companion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log

class CarnivalNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CarnivalReceiver", "Received broadcast: ${intent.action}")
        Log.d("CarnivalReceiver", "Intent extras: ${intent.extras}")
        
        // Acquire wake lock to ensure notification processing
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "CarnivalApp::NotificationWakeLock"
        )
        
        wakeLock.acquire(10*60*1000L /*10 minutes*/)
        
        try {
        val notificationService = AndroidNotificationService(context)
        
        when (intent.action) {
            "org.bridgwatercarnival.companion.DAILY_NOTIFICATION" -> {
                Log.d("CarnivalReceiver", "Processing daily countdown notification")
                val daysUntil = notificationService.calculateDaysUntilNovemberFirst()
                Log.d("CarnivalReceiver", "Days until: $daysUntil")
                notificationService.showCountdownNotification(daysUntil)
                    // Schedule next notification
                notificationService.scheduleDailyCountdownNotification()
            }
            // Commented out voting and results notifications
            // "org.bridgwatercarnival.companion.VOTING_NOTIFICATION" -> {
            //     Log.d("CarnivalReceiver", "Processing voting notification")
            //     notificationService.showVotingNotification()
            // }
            // "org.bridgwatercarnival.companion.RESULTS_NOTIFICATION" -> {
            //     Log.d("CarnivalReceiver", "Processing results notification")
            //     notificationService.showResultsNotification()
            // }
                Intent.ACTION_BOOT_COMPLETED -> {
                    Log.d("CarnivalReceiver", "Device booted, rescheduling notifications")
                    // Reschedule notifications after device boot
                    if (notificationService.isNotificationPermissionGranted()) {
                        notificationService.scheduleDailyCountdownNotification()
                    // Commented out voting and results notification scheduling
                    // notificationService.scheduleVotingNotifications()
                    // notificationService.scheduleResultsNotifications()
                    }
                }
            else -> {
                Log.d("CarnivalReceiver", "Unknown action: ${intent.action}")
            }
            }
        } finally {
            wakeLock.release()
        }
    }
} 