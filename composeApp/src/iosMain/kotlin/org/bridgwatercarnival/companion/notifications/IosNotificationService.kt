package org.bridgwatercarnival.companion.notifications

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.UserNotifications.*
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIApplicationSignificantTimeChangeNotification
import platform.UIKit.registerForRemoteNotifications
import platform.darwin.NSObject
import kotlin.concurrent.Volatile
import org.bridgwatercarnival.companion.util.Storage
import org.bridgwatercarnival.companion.util.StorageKeys

class IosNotificationService : NotificationService {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    @Volatile
    private var delegate: NSObject? = null
    private var lastNotificationCheck: NSDate? = null

    init {
        setupNotificationDelegate()

        // Add observer for significant time changes (midnight)
        NSNotificationCenter.defaultCenter.addObserverForName(
            UIApplicationSignificantTimeChangeNotification,
            null,
            null
        ) { _ ->
            println("Significant time change detected")
            // Only reschedule if notifications are enabled AND user has countdown notifications enabled
            if (isNotificationPermissionGranted() && 
                Storage.getBoolean(StorageKeys.COUNTDOWN_NOTIFICATIONS_ENABLED, true) && 
                shouldRescheduleNotifications()) {
                println("Rescheduling notifications after significant time change")
                cancelDailyCountdownNotification()
                scheduleDailyCountdownNotification()
            } else {
                println("Not rescheduling notifications - permission: ${isNotificationPermissionGranted()}, countdown enabled: ${Storage.getBoolean(StorageKeys.COUNTDOWN_NOTIFICATIONS_ENABLED, true)}")
            }
        }

        // Remove the app activation observer as it was causing too frequent rescheduling
    }

    private fun shouldRescheduleNotifications(): Boolean {
        val now = NSDate()
        val lastCheck = lastNotificationCheck ?: return true

        // Calculate time difference in hours
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(
            NSCalendarUnitHour.toULong(),
            lastCheck,
            now,
            0.toULong()
        )

        // Only reschedule if it's been at least 12 hours
        val hoursSinceLastCheck = components.hour.toInt()
        return hoursSinceLastCheck >= 12
    }

    private fun updateLastNotificationCheck() {
        lastNotificationCheck = NSDate()
    }

    private fun setupNotificationDelegate() {
        val newDelegate = object : NSObject(), UNUserNotificationCenterDelegateProtocol {
            override fun userNotificationCenter(
                center: UNUserNotificationCenter,
                willPresentNotification: UNNotification,
                withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
            ) {
                println("Notification will present in foreground")

                // Check if countdown notifications are enabled by user
                if (!Storage.getBoolean(StorageKeys.COUNTDOWN_NOTIFICATIONS_ENABLED, true)) {
                    println("Countdown notifications disabled by user, not showing notification")
                    withCompletionHandler(0.toULong()) // Don't show notification
                    return
                }

                // Update the notification content with current countdown before showing
                if (willPresentNotification.request.identifier == "daily_countdown") {
                    val daysUntil = calculateDaysUntilNovemberFirst(NSDate())
                    val notificationText = when {
                        daysUntil == 0 -> "Carnival Day is here 🎉"
                        daysUntil == 1 -> "Tomorrow is Bridgwater Carnival 👀"
                        else -> "$daysUntil days until Bridgwater Carnival 🎇"
                    }

                    // Create updated notification content
                    val updatedContent = UNMutableNotificationContent().apply {
                        setTitle("Carnival Countdown")
                        setBody(notificationText)
                        setSound(UNNotificationSound.defaultSound)
                        setBadge(NSNumber(1))
                        setCategoryIdentifier("carnival_notification")
                        setInterruptionLevel(UNNotificationInterruptionLevel.UNNotificationInterruptionLevelActive)
                        setThreadIdentifier("carnival_notifications")
                    }

                    // Create updated request with current trigger
                    val updatedRequest = UNNotificationRequest.requestWithIdentifier(
                        "daily_countdown",
                        updatedContent,
                        willPresentNotification.request.trigger
                    )

                    // Remove existing and add updated notification
                    center.removePendingNotificationRequestsWithIdentifiers(listOf("daily_countdown"))
                    center.addNotificationRequest(updatedRequest) { error ->
                        if (error != null) {
                            println("Error updating notification: ${error.localizedDescription}")
                        } else {
                            println("Successfully updated notification for $daysUntil days")
                        }
                    }
                }

                // Show the notification
                withCompletionHandler(
                    UNNotificationPresentationOptionBanner.toULong()
                        .or(UNNotificationPresentationOptionSound.toULong())
                        .or(UNNotificationPresentationOptionList.toULong())
                        .or(UNNotificationPresentationOptionBadge.toULong())
                )
            }

            override fun userNotificationCenter(
                center: UNUserNotificationCenter,
                didReceiveNotificationResponse: UNNotificationResponse,
                withCompletionHandler: () -> Unit
            ) {
                // When user interacts with notification, update the countdown
                if (didReceiveNotificationResponse.notification.request.identifier == "daily_countdown") {
                    // Only reschedule if countdown notifications are enabled by user
                    if (Storage.getBoolean(StorageKeys.COUNTDOWN_NOTIFICATIONS_ENABLED, true)) {
                        val daysUntil = calculateDaysUntilNovemberFirst(NSDate())
                        scheduleDailyCountdownNotification()
                    }
                }
                withCompletionHandler()
            }

            override fun userNotificationCenter(
                center: UNUserNotificationCenter,
                openSettingsForNotification: UNNotification?
            ) {
                println("Settings opened for notification")
                checkAndUpdateNotificationStatus()
            }
        }
        delegate = newDelegate
        notificationCenter.delegate = newDelegate
    }

    private fun checkAndUpdateNotificationStatus() {
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            settings?.let {
                val isEnabled = it.authorizationStatus == UNAuthorizationStatusAuthorized ||
                        it.authorizationStatus == UNAuthorizationStatusProvisional
                println("Notification status updated: $isEnabled")
                if (isEnabled) {
                    // Show immediate notification if enabled
                    showTestNotification()
                }
            }
        }
    }

    private fun calculateDaysUntilNovemberFirst(date: NSDate = NSDate()): Int {
        val calendar = NSCalendar.currentCalendar

        // Set timezone to London
        val londonTimeZone = NSTimeZone.timeZoneWithName("Europe/London")!!
        calendar.timeZone = londonTimeZone

        // Get components for the current date
        val currentComponents = calendar.components(
            NSCalendarUnitYear.toULong()
                .or(NSCalendarUnitMonth.toULong())
                .or(NSCalendarUnitDay.toULong())
                .or(NSCalendarUnitHour.toULong())
                .or(NSCalendarUnitMinute.toULong())
                .or(NSCalendarUnitSecond.toULong()),
            date
        )

        // Create components for this year's November 1st
        val targetComponents = NSDateComponents().apply {
            setYear(currentComponents.year)
            setMonth(11) // November
            setDay(1)
            setHour(0)
            setMinute(0)
            setSecond(0)
            setTimeZone(londonTimeZone)
        }

        // Get the date for this year's November 1st
        var targetDate = calendar.dateFromComponents(targetComponents)!!

        // If we're past November 1st, add a year
        if (date.compare(targetDate) == NSOrderedDescending) {
            targetComponents.setYear(currentComponents.year + 1)
            targetDate = calendar.dateFromComponents(targetComponents)!!
        }

        // Set both dates to start of day for accurate calculation
        val startOfToday = calendar.dateFromComponents(
            calendar.components(
                NSCalendarUnitYear.toULong()
                    .or(NSCalendarUnitMonth.toULong())
                    .or(NSCalendarUnitDay.toULong()),
                date
            )
        )!!

        val startOfTarget = calendar.dateFromComponents(
            calendar.components(
                NSCalendarUnitYear.toULong()
                    .or(NSCalendarUnitMonth.toULong())
                    .or(NSCalendarUnitDay.toULong()),
                targetDate
            )
        )!!

        // Calculate days between
        val daysBetween = calendar.components(
            NSCalendarUnitDay.toULong(),
            startOfToday,
            startOfTarget,
            0.toULong()
        )

        println("Current date: $date")
        println("Start of today: $startOfToday")
        println("Target date: $targetDate")
        println("Start of target: $startOfTarget")
        println("Days until carnival: ${daysBetween.day}")

        return daysBetween.day.toInt()
    }

    private fun getNotificationText(daysUntil: Int, date: NSDate = NSDate()): String {
        val calendar = NSCalendar.currentCalendar.apply {
            timeZone = NSTimeZone.timeZoneWithName("Europe/London")!!
        }

        val components = calendar.components(
            NSCalendarUnitMonth.toULong()
                .or(NSCalendarUnitDay.toULong()),
            date
        )

        return when {
            components.month.toInt() == 11 && components.day.toInt() == 1 -> "Carnival Day is here 🎉, take care and travel to the event safely ❤️ Make sure you arrive early, car parks get full quite quickly ⏳ ENJOY!!! 😁"
            components.month.toInt() == 10 && components.day.toInt() == 31 -> "Tomorrow is Bridgwater Carnival 👀, Make sure you are prepared for the day! 💪"
            components.month.toInt() == 10 && components.day.toInt() == 25 -> " One Week to Go 🤩!! Have you planned your visit yet? 🤨 If not, start planning your journey now by using our planner and  map to look for local shops 🛍️, cafes 🍽️, car parks 🅿️, and a lot more! 📆"

            daysUntil == 0 -> "Carnival Day is here 🎉"
            daysUntil == 1 -> "Tomorrow is Bridgwater Carnival 👀"
            else -> "$daysUntil days until Bridgwater Carnival 🎇"
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun scheduleDailyCountdownNotification() {
        println("Scheduling daily countdown notification...")
        
        // Check if countdown notifications are enabled by user
        if (!Storage.getBoolean(StorageKeys.COUNTDOWN_NOTIFICATIONS_ENABLED, true)) {
            println("Countdown notifications are disabled by user, not scheduling")
            return
        }
        
        updateLastNotificationCheck()

        // Create components for next 9 AM in London time
        val calendar = NSCalendar.currentCalendar.apply {
            timeZone = NSTimeZone.timeZoneWithName("Europe/London")!!
        }

        // Get current date and time
        val now = NSDate()
        println("Current date for scheduling: $now")

        // Create components for next 9 AM
        val baseComponents = calendar.components(
            NSCalendarUnitYear.toULong()
                .or(NSCalendarUnitMonth.toULong())
                .or(NSCalendarUnitDay.toULong())
                .or(NSCalendarUnitHour.toULong())
                .or(NSCalendarUnitMinute.toULong())
                .or(NSCalendarUnitSecond.toULong()),
            now
        ).apply {
            setHour(9)
            setMinute(0)
            setSecond(0)
            setTimeZone(NSTimeZone.timeZoneWithName("Europe/London")!!)
        }

        // If it's already past 9 AM London time, schedule for tomorrow
        val currentHour = calendar.components(
            NSCalendarUnitHour.toULong(),
            now
        ).hour.toInt()

        if (currentHour >= 9) {
            baseComponents.setDay(baseComponents.day + 1)
        }

        // Instead of using a repeating trigger, we'll schedule individual notifications
        // for the next 7 days to ensure they're always up to date
        for (i in 0..6) {
            // Create new components for this day
            val dayComponents = NSDateComponents().apply {
                setYear(baseComponents.year)
                setMonth(baseComponents.month)
                setDay(baseComponents.day + i)
                setHour(9)
                setMinute(0)
                setSecond(0)
                setTimeZone(NSTimeZone.timeZoneWithName("Europe/London")!!)
            }

            // Create trigger for this specific day
            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dayComponents,
                false // Don't repeat
            )

            // Calculate days until carnival for this specific day
            val targetDate = calendar.dateFromComponents(dayComponents)!!
            val daysUntil = calculateDaysUntilNovemberFirst(targetDate)

            // Create notification content
            val content = UNMutableNotificationContent().apply {
                val notificationText = when {
                    calendar.components(
                        NSCalendarUnitMonth.toULong()
                            .or(NSCalendarUnitDay.toULong()),
                        targetDate
                    ).let { it.month.toInt() == 11 && it.day.toInt() == 1 } -> "Carnival Day is here 🎉"
                    calendar.components(
                        NSCalendarUnitMonth.toULong()
                            .or(NSCalendarUnitDay.toULong()),
                        targetDate
                    ).let { it.month.toInt() == 10 && it.day.toInt() == 31 } -> "Tomorrow is Bridgwater Carnival 👀"
                    daysUntil == 0 -> "Carnival Day is here 🎉"
                    daysUntil == 1 -> "Tomorrow is Bridgwater Carnival 👀"
                    else -> "$daysUntil days until Bridgwater Carnival 🎇"
                }

                setTitle("Carnival Countdown")
                setBody(notificationText)
                setSound(UNNotificationSound.defaultSound)
                setBadge(NSNumber(1))
                setCategoryIdentifier("carnival_notification")
                setInterruptionLevel(UNNotificationInterruptionLevel.UNNotificationInterruptionLevelActive)
                setThreadIdentifier("carnival_notifications")
            }

            // Create the notification request with a unique identifier for each day
            val request = UNNotificationRequest.requestWithIdentifier(
                "daily_countdown_$i",
                content,
                trigger
            )

            // Schedule the notification
            notificationCenter.addNotificationRequest(request) { error ->
                if (error != null) {
                    println("Error scheduling daily notification for day $i: ${error.localizedDescription}")
                } else {
                    println("Successfully scheduled daily notification for day $i with $daysUntil days until carnival")
                }
            }
        }

        // Show an immediate test notification
        showTestNotification()
    }

    override fun cancelDailyCountdownNotification() {
        println("Cancelling countdown notifications...")
        // Create list of all possible notification identifiers
        val notificationIds = (0..6).map { "daily_countdown_$it" }.toList()

        // Remove all pending notifications
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(notificationIds)
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(notificationIds)
        println("Cancelled countdown notifications")
    }

    override fun openNotificationSettings() {
        // First request notification permissions if not already granted
        requestNotificationPermission()

        // Open iOS Settings app using the correct URL scheme
        val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        if (settingsUrl != null) {
            UIApplication.sharedApplication.openURL(
                url = settingsUrl,
                options = mapOf<Any?, Any?>(),
                completionHandler = { success ->
                    println("Opened settings with result: $success")
                }
            )
        }
    }

    override fun isNotificationPermissionGranted(): Boolean {
        var isGranted = false
        val semaphore = NSCondition()

        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            settings?.let {
                isGranted = it.authorizationStatus == UNAuthorizationStatusAuthorized ||
                        it.authorizationStatus == UNAuthorizationStatusProvisional
            }
            semaphore.signal()
        }

        semaphore.wait()
        return isGranted
    }

    override fun requestNotificationPermission() {
        // Request permissions with standard options
        notificationCenter.requestAuthorizationWithOptions(
            UNAuthorizationOptionSound.toULong()
                .or(UNAuthorizationOptionAlert.toULong())
                .or(UNAuthorizationOptionBadge.toULong())
                .or(UNAuthorizationOptionProvisional.toULong())
        ) { granted, error ->
            if (error != null) {
                println("Error requesting notification permission: ${error.localizedDescription}")
            } else {
                println("Notification permission granted: $granted")
                if (granted) {
                    // Register for remote notifications
                    UIApplication.sharedApplication.registerForRemoteNotifications()

                    // Only show notifications if countdown notifications are enabled by user
                    if (Storage.getBoolean(StorageKeys.COUNTDOWN_NOTIFICATIONS_ENABLED, true)) {
                        // Show immediate countdown notification
                        val daysUntil = calculateDaysUntilNovemberFirst()
                        showCountdownNotification(daysUntil)

                        // Schedule the daily notification
                        scheduleDailyCountdownNotification()
                    } else {
                        println("Notification permission granted but countdown notifications disabled by user")
                    }
                } else {
                    // If permission denied, open settings
                    openNotificationSettings()
                }
            }
        }
    }

    override fun showTestNotification() {
        // Only show test notification if countdown notifications are enabled by user
        if (!Storage.getBoolean(StorageKeys.COUNTDOWN_NOTIFICATIONS_ENABLED, true)) {
            println("Countdown notifications are disabled by user, not showing test notification")
            return
        }
        
        // Show immediate countdown notification
        val now = NSDate()
        val daysUntil = calculateDaysUntilNovemberFirst(now)
        println("Testing notification with current date: $now")
        println("Days until carnival: $daysUntil")
        showCountdownNotification(daysUntil)
    }

    fun scheduleVotingNotifications() {
        println("Scheduling voting notifications...")

        // Schedule voting open notification
        val votingOpenContent = UNMutableNotificationContent().apply {
            setTitle("Voting is Open!")
            setBody("Voting for Bridgwater Carnival is now open. Cast your vote now!")
            setSound(UNNotificationSound.defaultSound)
            setBadge(NSNumber(1))
            setCategoryIdentifier("carnival_notification")
            setInterruptionLevel(UNNotificationInterruptionLevel.UNNotificationInterruptionLevelActive)
            setThreadIdentifier("carnival_notifications")
        }

        // Create components for voting time (10 PM on November 1st)
        val calendar = NSCalendar.currentCalendar.apply {
            timeZone = NSTimeZone.timeZoneWithName("Europe/London")!!
        }
        val votingComponents = calendar.components(
            NSCalendarUnitYear.toULong()
                .or(NSCalendarUnitMonth.toULong())
                .or(NSCalendarUnitDay.toULong())
                .or(NSCalendarUnitHour.toULong())
                .or(NSCalendarUnitMinute.toULong()),
            NSDate()
        ).apply {
            setMonth(11) // November
            setDay(1)
            setHour(22) // 10 PM
            setMinute(0)
        }

        val votingTrigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            votingComponents,
            false
        )

        val votingRequest = UNNotificationRequest.requestWithIdentifier(
            "voting_open",
            votingOpenContent,
            votingTrigger
        )

        notificationCenter.addNotificationRequest(votingRequest) { error ->
            if (error != null) {
                println("Error scheduling voting notification: ${error.localizedDescription}")
            } else {
                println("Successfully scheduled voting notification")
            }
        }
    }

    fun cancelVotingNotifications() {
        println("Cancelling voting notifications...")
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf("voting_open")
        )
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(
            listOf("voting_open")
        )
    }

    fun scheduleResultsNotifications() {
        println("Scheduling results notifications...")

        // Schedule results available notification
        val resultsContent = UNMutableNotificationContent().apply {
            setTitle("Results Available 🥁")
            setBody("The results for Bridgwater Carnival are now available. Check them out👀")
            setSound(UNNotificationSound.defaultSound)
            setBadge(NSNumber(1))
            setCategoryIdentifier("carnival_notification")
            setInterruptionLevel(UNNotificationInterruptionLevel.UNNotificationInterruptionLevelActive)
            setThreadIdentifier("carnival_notifications")
        }

        // Create components for results time (12 AM on November 2nd)
        val calendar = NSCalendar.currentCalendar.apply {
            timeZone = NSTimeZone.timeZoneWithName("Europe/London")!!
        }
        val resultsComponents = calendar.components(
            NSCalendarUnitYear.toULong()
                .or(NSCalendarUnitMonth.toULong())
                .or(NSCalendarUnitDay.toULong())
                .or(NSCalendarUnitHour.toULong())
                .or(NSCalendarUnitMinute.toULong()),
            NSDate()
        ).apply {
            setMonth(11) // November
            setDay(2)
            setHour(0) // 12 AM
            setMinute(0)
        }

        val resultsTrigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            resultsComponents,
            false
        )

        val resultsRequest = UNNotificationRequest.requestWithIdentifier(
            "results_available",
            resultsContent,
            resultsTrigger
        )

        notificationCenter.addNotificationRequest(resultsRequest) { error ->
            if (error != null) {
                println("Error scheduling results notification: ${error.localizedDescription}")
            } else {
                println("Successfully scheduled results notification")
            }
        }
    }

    fun cancelResultsNotifications() {
        println("Cancelling results notifications...")
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf("results_available")
        )
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(
            listOf("results_available")
        )
    }

    private fun showCountdownNotification(daysUntil: Int) {
        println("Showing countdown notification for $daysUntil days")
        val content = UNMutableNotificationContent().apply {
            setTitle("Carnival Countdown")
            setBody(getNotificationText(daysUntil))
            setSound(UNNotificationSound.defaultSound)
            setBadge(NSNumber(1))
            setCategoryIdentifier("carnival_notification")
            setInterruptionLevel(UNNotificationInterruptionLevel.UNNotificationInterruptionLevelActive)
            setThreadIdentifier("carnival_notifications")
        }

        // Show notification immediately (1 second delay to ensure it's processed)
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)

        val request = UNNotificationRequest.requestWithIdentifier(
            "countdown_notification",
            content,
            trigger
        )

        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Error showing countdown notification: ${error.localizedDescription}")
            } else {
                println("Successfully scheduled countdown notification for $daysUntil days")
            }
        }
    }
}