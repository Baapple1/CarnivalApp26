package org.bridgwatercarnival.companion.util

import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import org.bridgwatercarnival.companion.database.FirestoreService

object TimeWindow {
    /**
     * When true:
     * - Enables unlimited voting (users can vote multiple times)
     */

    var TEST_MODE = false //set true to enable voting with  infinate votes
        set(value) {
            field = value
            // Synchronize with FirestoreService test mode
            org.bridgwatercarnival.companion.database.FirestoreService.TEST_MODE = value
        }

    //set all 3 to true to test voting and see results
    // 1) Set this to true and the votingStart will be set to the current time, votingEnd to 2 hours later
    var USE_CURRENT_TIME_FOR_TESTING = false

    // 2) Set this to true to see results while still being able to vote
    var SHOW_RESULTS_IN_TESTING = false

    // New flag to enable both voting and results simultaneously - enable both current time and shwo results - set testing to true first
    var ENABLE_CONCURRENT_VOTING_AND_RESULTS = false

    // Initialize FirestoreService.TEST_MODE
    init {
        org.bridgwatercarnival.companion.database.FirestoreService.TEST_MODE = TEST_MODE
    }

    private val londonZone = TimeZone.of("Europe/London")

    // e.g., for a 22:00 London display time, we set 21:00- change to current time to test
    private val votingStart = LocalDateTime(2025, 11, 1, 22, 0) // Will show as 10:00 PM (22:00) London time
    private val votingEnd = LocalDateTime(2025, 11, 2, 0, 0)   // Will show as 12:00 AM (00:00) London time
    // Will show as 12:00 AM (00:00) London time


    private val resultsStart = votingEnd

    private suspend fun getServerTime(): Instant {
        return try {
            // Get server timestamp from Firestore
            val timestamp = FirestoreService().getServerTimestamp()
            Instant.fromEpochMilliseconds(timestamp)
        } catch (e: Exception) {
            // Fallback to system time if server time fetch fails
            Clock.System.now()
        }
    }

    suspend fun isVotingOpen(): Boolean {
        if (TEST_MODE) return true // Bypass time check in test mode

        val currentInstant = getServerTime()
        val currentLondonTime = currentInstant.toLocalDateTime(londonZone)

        if (USE_CURRENT_TIME_FOR_TESTING) {
            // Create test window from 5 minutes ago to 2 hours from now
            val fiveMinutesAgo = Clock.System.now().minus(5.minutes)
            val twoHoursLater = Clock.System.now().plus(2.hours)
            val testStartTime = fiveMinutesAgo.toLocalDateTime(londonZone)
            val testEndTime = twoHoursLater.toLocalDateTime(londonZone)
            return currentLondonTime in testStartTime..testEndTime
        }

        return currentLondonTime in votingStart..votingEnd
    }

    suspend fun hasVotingEnded(): Boolean {
        if (TEST_MODE) return false // Bypass time check in test mode

        // Modified to allow voting even when results are shown if concurrent mode is enabled
        if (SHOW_RESULTS_IN_TESTING && ENABLE_CONCURRENT_VOTING_AND_RESULTS) return false

        // Force voting to be ended when we want to show results in testing (only if concurrent mode is disabled)
        if (SHOW_RESULTS_IN_TESTING && !ENABLE_CONCURRENT_VOTING_AND_RESULTS) return true

        val currentInstant = getServerTime()
        val currentLondonTime = currentInstant.toLocalDateTime(londonZone)

        if (USE_CURRENT_TIME_FOR_TESTING) {
            // Voting ends 2 hours from now
            val twoHoursLater = Clock.System.now().plus(2.hours)
            val testEndTime = twoHoursLater.toLocalDateTime(londonZone)
            return currentLondonTime > testEndTime
        }

        return currentLondonTime > votingEnd
    }

    suspend fun areResultsOpen(): Boolean {
        if (TEST_MODE) return true // Bypass time check in test mode

        // Force results to be available in testing
        if (SHOW_RESULTS_IN_TESTING) return true

        val currentInstant = getServerTime()
        val currentLondonTime = currentInstant.toLocalDateTime(londonZone)

        if (USE_CURRENT_TIME_FOR_TESTING) {
            // Results available 2 hours from now
            val twoHoursLater = Clock.System.now().plus(2.hours)
            val testResultsTime = twoHoursLater.toLocalDateTime(londonZone)
            return currentLondonTime >= testResultsTime
        }

        return currentLondonTime >= resultsStart
    }

    suspend fun hasResultsEnded(): Boolean {
        if (TEST_MODE) return false // Bypass time check in test mode

        val currentInstant = getServerTime()
        val currentLondonTime = currentInstant.toLocalDateTime(londonZone)

        if (USE_CURRENT_TIME_FOR_TESTING) {
            // Results end 2 hours from now
            val twoHoursLater = Clock.System.now().plus(2.hours)
            val testResultsEndTime = twoHoursLater.toLocalDateTime(londonZone)
            return currentLondonTime > testResultsEndTime
        }

        // Results end 24 hours after the carnival
        val resultsEndTime = resultsStart.toInstant(londonZone).plus(24.hours).toLocalDateTime(londonZone)
        return currentLondonTime > resultsEndTime
    }

    suspend fun getTimeUntilVoting(): String {
        if (TEST_MODE) return "Voting is open (Test Mode)"

        // Show voting open message when using current time for testing
        if (USE_CURRENT_TIME_FOR_TESTING) return "Voting is open (Testing)"

        val currentInstant = getServerTime()
        val currentLondonTime = currentInstant.toLocalDateTime(londonZone)
        if (currentLondonTime >= votingStart) return "Voting is open"

        val votingStartInstant = votingStart.toInstant(londonZone)
        val duration = votingStartInstant - currentInstant

        return formatDuration(duration)
    }

    suspend fun getTimeUntilResults(): String {
        if (TEST_MODE) return "Results are available (Test Mode)"

        // Show results available message when testing results
        if (SHOW_RESULTS_IN_TESTING) return "Results are available (Testing Results)"

        val currentInstant = getServerTime()
        val currentLondonTime = currentInstant.toLocalDateTime(londonZone)
        if (currentLondonTime >= resultsStart) return "Results are available"

        val resultsStartInstant = resultsStart.toInstant(londonZone)
        val duration = resultsStartInstant - currentInstant

        return formatDuration(duration)
    }

    private fun formatDuration(duration: Duration): String {
        val days = duration.inWholeDays
        val hours = duration.inWholeHours % 24
        val minutes = duration.inWholeMinutes % 60
        val seconds = duration.inWholeSeconds % 60

        return buildString {
            if (days > 0) append("$days days ")
            if (hours > 0) append("$hours hours ")
            if (minutes > 0) append("$minutes minutes ")
            append("$seconds seconds")
        }.trim()
    }
} 