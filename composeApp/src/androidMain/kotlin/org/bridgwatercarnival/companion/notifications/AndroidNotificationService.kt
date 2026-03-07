package org.bridgwatercarnival.companion.notifications

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bridgwatercarnival.companion.MainActivity
import org.bridgwatercarnival.companion.R
import java.util.*

class AndroidNotificationService(private val context: Context) : NotificationService {
    companion object {
        private const val CHANNEL_ID = "carnival_countdown"
        private const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE = 0
        private const val PERMISSION_REQUEST_CODE = 123
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val name = "Carnival Countdown"
                val descriptionText = "Daily notifications about time remaining until Bridgwater Carnival"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    enableLights(true)
                    enableVibration(true)
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            } catch (e: Exception) {
                println("Error creating notification channel: ${e.message}")
            }
        }
    }

    override fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context is Activity) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        PERMISSION_REQUEST_CODE
                    )
                } else {
                    // Permission already granted, schedule notifications
                    val daysUntil = calculateDaysUntilNovemberFirst()
                    showCountdownNotification(daysUntil) // Show first notification immediately
                    scheduleDailyCountdownNotification() // Set up daily notifications
                }
            } else {
                println("Context is not an Activity, cannot request permissions")
            }
        } else {
            // For pre-Android 13, just schedule notifications
            val daysUntil = calculateDaysUntilNovemberFirst()
            showCountdownNotification(daysUntil) // Show first notification immediately
            scheduleDailyCountdownNotification() // Set up daily notifications
        }
    }

    override fun scheduleDailyCountdownNotification() {
        Log.d("CarnivalNotifications", "Scheduling daily countdown notification...")

        // Show immediate notification first
        showTestNotification()

        // Then schedule daily notifications
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CarnivalNotificationReceiver::class.java).apply {
            action = "org.bridgwatercarnival.companion.DAILY_NOTIFICATION"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set alarm for 9 AM daily in London time
        val londonTimeZone = TimeZone.getTimeZone("Europe/London")
        val calendar = Calendar.getInstance(londonTimeZone).apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            // If it's past 9 AM London time, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        Log.d("CarnivalNotifications", "Current time: ${Date(System.currentTimeMillis())}")
        Log.d("CarnivalNotifications", "Daily notification scheduled for: ${Date(calendar.timeInMillis)}")

        try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                    // Use setAlarmClock for more reliable delivery
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent),
                        pendingIntent
                    )
                    Log.d("CarnivalNotifications", "Daily notification scheduled with setAlarmClock")
                } else {
                    // Fallback to inexact repeating alarm
                    alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
                    Log.d("CarnivalNotifications", "Daily notification scheduled with setInexactRepeating")
                }
            } else {
                // For older Android versions, use setAlarmClock
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent),
                    pendingIntent
                )
                Log.d("CarnivalNotifications", "Daily notification scheduled with setAlarmClock")
            }
        } catch (e: Exception) {
            Log.e("CarnivalNotifications", "Error scheduling notification: ${e.message}")
            // Fallback to inexact repeating alarm
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            Log.d("CarnivalNotifications", "Daily notification scheduled with fallback method")
        }
    }

    override fun cancelDailyCountdownNotification() {
        try {
            // Cancel the alarm
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val intent = Intent(context, CarnivalNotificationReceiver::class.java).apply {
                action = "org.bridgwatercarnival.companion.DAILY_NOTIFICATION"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()

            // Remove any existing notifications
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(NOTIFICATION_ID)

            println("Notifications cancelled successfully")
        } catch (e: Exception) {
            println("Error canceling notification: ${e.message}")
        }
    }

    override fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    internal fun calculateDaysUntilNovemberFirst(): Int {
        val londonTimeZone = TimeZone.getTimeZone("Europe/London")
        val today = Calendar.getInstance(londonTimeZone)
        val carnivalDay = Calendar.getInstance(londonTimeZone)

        // Set carnival day to November 1st of current year
        carnivalDay.set(Calendar.MONTH, Calendar.NOVEMBER)
        carnivalDay.set(Calendar.DAY_OF_MONTH, 1)
        carnivalDay.set(Calendar.HOUR_OF_DAY, 0)
        carnivalDay.set(Calendar.MINUTE, 0)
        carnivalDay.set(Calendar.SECOND, 0)
        carnivalDay.set(Calendar.MILLISECOND, 0)

        // Set today to start of day for accurate calculation
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        // If we're past this year's carnival, set to next year
        if (today.after(carnivalDay)) {
            carnivalDay.add(Calendar.YEAR, 1)
        }

        // Calculate the difference in days using the timezone-adjusted dates
        val diff = carnivalDay.timeInMillis - today.timeInMillis
        val daysUntil = (diff / (24 * 60 * 60 * 1000L)).toInt()

        Log.d("CarnivalNotifications", "Today (London time): ${today.time}")
        Log.d("CarnivalNotifications", "Carnival Day (London time): ${carnivalDay.time}")
        Log.d("CarnivalNotifications", "Days until: $daysUntil")

        return daysUntil
    }

    fun showCountdownNotification(daysUntil: Int) {
        if (!isNotificationPermissionGranted()) {
            return
        }

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // Get current date in London timezone
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"))
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val notificationText = when {
                month == Calendar.NOVEMBER && day == 1 -> "Carnival Day is here 🎉, take care and travel to the event safely ❤️ Make sure you arrive early, car parks get full quite quickly ⏳ ENJOY!!! 😁"
                month == Calendar.OCTOBER && day == 25 -> "One Week to Go 🤩!! Have you planned your visit yet? 🤨 If not, start planning your journey now by using our planner and  map to look for local shops 🛍️, cafes 🍽️, car parks 🅿️, and a lot more! 📆"
                month == Calendar.OCTOBER && day == 31 -> "Tomorrow is Bridgwater Carnival 👀 Make sure you are prepared for the day! 💪"
                daysUntil == 0 -> "Carnival Day is here 🎉"
                daysUntil == 1 -> "Tomorrow is Bridgwater Carnival 👀"
                else -> "$daysUntil days until Bridgwater Carnival 🎇"
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // Required monochrome icon for status bar
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.guy_fawkes_icon))  // Full color app icon
                .setContentTitle("Carnival Countdown")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()

            @Suppress("MissingPermission")
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            println("Security exception when showing notification: ${e.message}")
        } catch (e: Exception) {
            println("Error showing notification: ${e.message}")
        }
    }

    override fun showTestNotification() {
        // Show immediate countdown notification
        val daysUntil = calculateDaysUntilNovemberFirst()
        println("Testing notification with $daysUntil days until carnival")
        showCountdownNotification(daysUntil)
    }

    override fun openNotificationSettings() {
        // For Android, we don't need to open settings as we handle permissions directly
        // This is just a no-op implementation
    }

    fun scheduleVotingNotifications() {
        Log.d("CarnivalNotifications", "Scheduling voting notifications...")

        // Check if we're past voting time
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 22) // 10 PM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        Log.d("CarnivalNotifications", "Current time: ${Date(System.currentTimeMillis())}")
        Log.d("CarnivalNotifications", "Voting notification scheduled for: ${Date(calendar.timeInMillis)}")

        // If we're past voting time, don't schedule
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            Log.d("CarnivalNotifications", "Voting time has passed, not scheduling notification")
            return
        }

        // Schedule voting open notification
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CarnivalNotificationReceiver::class.java).apply {
            action = "org.bridgwatercarnival.companion.VOTING_NOTIFICATION"
            putExtra("notification_type", "voting")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.d("CarnivalNotifications", "Voting notification scheduled successfully")
            } else {
                Log.e("CarnivalNotifications", "Cannot schedule exact alarms")
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d("CarnivalNotifications", "Voting notification scheduled successfully")
        }
    }

    fun cancelVotingNotifications() {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, CarnivalNotificationReceiver::class.java).apply {
                action = "org.bridgwatercarnival.companion.VOTING_NOTIFICATION"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (e: Exception) {
            println("Error canceling voting notification: ${e.message}")
        }
    }

    fun scheduleResultsNotifications() {
        Log.d("CarnivalNotifications", "Scheduling results notifications...")

        // Check if we're past results time
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.DAY_OF_MONTH, 2)
            set(Calendar.HOUR_OF_DAY, 0) // 12 AM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        Log.d("CarnivalNotifications", "Current time: ${Date(System.currentTimeMillis())}")
        Log.d("CarnivalNotifications", "Results notification scheduled for: ${Date(calendar.timeInMillis)}")

        // If we're past results time, don't schedule
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            Log.d("CarnivalNotifications", "Results time has passed, not scheduling notification")
            return
        }

        // Schedule results available notification
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CarnivalNotificationReceiver::class.java).apply {
            action = "org.bridgwatercarnival.companion.RESULTS_NOTIFICATION"
            putExtra("notification_type", "results")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.d("CarnivalNotifications", "Results notification scheduled successfully")
            } else {
                Log.e("CarnivalNotifications", "Cannot schedule exact alarms")
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d("CarnivalNotifications", "Results notification scheduled successfully")
        }
    }

    fun cancelResultsNotifications() {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, CarnivalNotificationReceiver::class.java).apply {
                action = "org.bridgwatercarnival.companion.RESULTS_NOTIFICATION"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (e: Exception) {
            println("Error canceling results notification: ${e.message}")
        }
    }

    fun showVotingNotification() {
        if (!isNotificationPermissionGranted()) {
            return
        }

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.guy_fawkes_icon))
                .setContentTitle("Voting is Open!")
                .setContentText("Voting for Bridgwater Carnival is now open. Cast your vote now!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()

            @Suppress("MissingPermission")
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID + 1, notification)
        } catch (e: Exception) {
            println("Error showing voting notification: ${e.message}")
        }
    }

    fun showResultsNotification() {
        if (!isNotificationPermissionGranted()) {
            return
        }

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.guy_fawkes_icon))
                .setContentTitle("Results Available!")
                .setContentText("The results for Bridgwater Carnival are now available. Check them out!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()

            @Suppress("MissingPermission")
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID + 2, notification)
        } catch (e: Exception) {
            println("Error showing results notification: ${e.message}")
        }
    }
} 