package org.bridgwatercarnival.companion.database

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.FieldValue
import kotlinx.datetime.Clock
import org.bridgwatercarnival.companion.firebase.FirebaseInit

class FirestoreService {
    private val db = Firebase.firestore
    private val votesCollection = db.collection("votes")
    private val deviceVotesCollection = db.collection("device_votes")  // New collection for tracking devices
    private val clubVotesCollection = db.collection("ClubVotes") // New collection for club votes
    private val voteDocument = "5l38G5qL8A7AimWVtFkg"

    // Add this flag for testing
    companion object {
        var TEST_MODE = false  // change to true to enable infinite votes (disable device vote limit)
    }

    init {
        println("FirestoreService initialized")
        println("Collection path: ${votesCollection.path}")
        println("Document path: ${votesCollection.document(voteDocument).path}")
    }

    suspend fun getServerTimestamp(): Long {
        // In test mode, just return current time
        if (TEST_MODE) {
            return Clock.System.now().toEpochMilliseconds()
        }

        return try {
            val timestampDoc = db.collection("_timestamps").document("server_time")
            timestampDoc.set(mapOf("timestamp" to Clock.System.now().toEpochMilliseconds()))
            val snapshot = timestampDoc.get()
            (snapshot.get("timestamp") as? Long) ?: Clock.System.now().toEpochMilliseconds()
        } catch (e: Exception) {
            println("Error getting server timestamp: ${e.message}")
            Clock.System.now().toEpochMilliseconds()
        }
    }

    suspend fun hasUserVoted(deviceId: String): Boolean {
        // Always return false in test mode to allow multiple votes
        if (TEST_MODE) {
            println("Test mode enabled - allowing vote")
            return false
        }

        try {
            val deviceDoc = deviceVotesCollection.document(deviceId).get()
            return deviceDoc.exists
        } catch (e: Exception) {
            println("Error checking device vote status: ${e.message}")
            return false
        }
    }

    suspend fun submitVote(deviceId: String, clubId: String): Boolean {
        try {
            println("=== Starting vote submission ===")
            println("Device ID: $deviceId")
            println("Test mode enabled: $TEST_MODE")
            println("Club ID: $clubId")

            // In test mode, always allow the vote
            if (TEST_MODE) {
                println("Test mode is enabled - proceeding with vote")

                // Add the actual vote
                val voteData = mapOf(
                    "deviceId" to deviceId,
                    "clubId" to clubId,
                    "timestamp" to Clock.System.now().toEpochMilliseconds()
                )

                // Submit the vote
                votesCollection.add(voteData)

                // Update the club vote count
                updateClubVoteCount(clubId)

                println("Vote submitted successfully in test mode")
                return true
            }

            // Production mode logic
            val deviceVoteDoc = deviceVotesCollection.document(deviceId).get()
            if (deviceVoteDoc.exists) {
                println("Device has already voted - aborting")
                return false
            }

            // Create device vote record
            deviceVotesCollection.document(deviceId).set(
                mapOf(
                    "deviceId" to deviceId,
                    "timestamp" to Clock.System.now().toEpochMilliseconds()
                )
            )

            // Add the vote
            val voteData = mapOf(
                "deviceId" to deviceId,
                "clubId" to clubId,
                "timestamp" to Clock.System.now().toEpochMilliseconds()
            )

            votesCollection.add(voteData)
            updateClubVoteCount(clubId)

            println("Vote submitted successfully")
            return true
        } catch (e: Exception) {
            println("=== Error submitting vote ===")
            println("Error type: ${e::class.simpleName}")
            println("Error message: ${e.message}")
            throw e
        }
    }

    private suspend fun updateClubVoteCount(clubId: String) {
        try {
            val clubVoteDoc = clubVotesCollection.document(clubId)

            // Get the current document
            val currentDoc = clubVoteDoc.get()

            // Get current vote count or default to 0
            val currentVotes = if (currentDoc.exists) {
                (currentDoc.get("voteCount") as? Long)?.toInt() ?: 0
            } else {
                0
            }

            // Increment the count
            val newVoteCount = currentVotes + 1

            // Set the new vote count
            clubVoteDoc.set(
                mapOf(
                    "voteCount" to newVoteCount
                )
            )

            println("Club vote count updated for club: $clubId, new count: $newVoteCount")
        } catch (e: Exception) {
            println("Error updating club vote count: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun getVoteCount(clubId: String): Int {
        try {
            println("=== Getting vote count ===")
            println("Club ID: $clubId")

            val clubVoteDoc = clubVotesCollection.document(clubId).get()
            return if (clubVoteDoc.exists) {
                (clubVoteDoc.get("voteCount") as? Long)?.toInt() ?: 0
            } else {
                0
            }
        } catch (e: Exception) {
            println("=== Error getting vote count ===")
            println("Error type: ${e::class.simpleName}")
            println("Error message: ${e.message}")
            throw e
        }
    }
}