package org.bridgwatercarnival.companion.viewmodel

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.bridgwatercarnival.companion.database.FirestoreService
import org.bridgwatercarnival.companion.model.CarnivalClub
import org.bridgwatercarnival.companion.service.CarnivalClubService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.random.Random

class VotingViewModel {
    private val firestoreService = FirestoreService()

    private val _votingState = MutableStateFlow<VotingState>(VotingState.Initial)
    val votingState: StateFlow<VotingState> = _votingState.asStateFlow()

    // New state variable to control results visibility
    var resultsEnabled: Boolean = false // Set this to true or false based on your logic

    // Method to toggle results visibility
    fun toggleResultsVisibility(enabled: Boolean) {
        resultsEnabled = enabled
    }

    suspend fun submitVote(deviceId: String, club: CarnivalClub) {
        _votingState.value = VotingState.Loading
        try {
            // Check if the device has already voted
            if (firestoreService.hasUserVoted(deviceId)) {
                _votingState.value = VotingState.AlreadyVoted
                return // Exit if the user has already voted
            }

            // Proceed with vote submission
            val success = firestoreService.submitVote(deviceId, club.id)
            _votingState.value = if (success) {
                VotingState.Success
            } else {
                VotingState.Error("Failed to submit vote")
            }
        } catch (e: Exception) {
            _votingState.value = VotingState.Error(e.message ?: "Unknown error")
        }
    }

    fun setTestMode(enabled: Boolean) {
        FirestoreService.TEST_MODE = enabled
    }

    suspend fun getVoteCount(clubId: String): Int {
        return firestoreService.getVoteCount(clubId)
    }

    /**
     * Simulates bulk voting for testing purposes
     * @param numVotes Number of votes to simulate
     * @param clubs List of clubs to distribute votes among
     * @param progressCallback Optional callback to report progress
     */
    suspend fun simulateBulkVoting(
        numVotes: Int,
        clubs: List<CarnivalClub>,
        progressCallback: ((Int, Int) -> Unit)? = null
    ) {
        _votingState.value = VotingState.Loading
        try {
            // Enable test mode
            setTestMode(true)

            // Use a background dispatcher for better performance
            withContext(Dispatchers.IO) {
                var successCount = 0
                var failureCount = 0

                // Create a list of device IDs
                val deviceIds = List(numVotes) { "test_device_${Random.nextInt(1000000)}" }

                // Distribute votes among clubs
                deviceIds.forEachIndexed { index, deviceId ->
                    try {
                        // Randomly select a club
                        val selectedClub = clubs[Random.nextInt(clubs.size)]

                        // Submit the vote
                        val success = firestoreService.submitVote(deviceId, selectedClub.id)

                        if (success) {
                            successCount++
                        } else {
                            failureCount++
                        }

                        // Report progress
                        progressCallback?.invoke(index + 1, numVotes)
                    } catch (e: Exception) {
                        failureCount++
                        println("Error submitting vote for device $deviceId: ${e.message}")
                    }
                }

                // Update state with results
                _votingState.value = VotingState.BulkVoteComplete(successCount, failureCount)
            }
        } catch (e: Exception) {
            _votingState.value = VotingState.Error("Bulk voting failed: ${e.message}")
        } finally {
            // Disable test mode
            setTestMode(false)
        }
    }

    sealed class VotingState {
        object Initial : VotingState()
        object Loading : VotingState()
        object Success : VotingState()
        object AlreadyVoted : VotingState()
        data class Error(val message: String) : VotingState()
        data class BulkVoteComplete(val successCount: Int, val failureCount: Int) : VotingState()
    }
}