package org.bridgwatercarnival.companion.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.auth.auth

object FirebaseInit {
    suspend fun initializeFirebase() {
        try {
            println("Checking Firebase initialization...")
            
            // Try to access Firestore to check if Firebase is initialized
            try {
                Firebase.firestore
                println("Firebase already initialized, skipping...")
                return
            } catch (_: Exception) {
                // Firebase not initialized, continue with initialization
            }

            println("Initializing Firebase...")
            val options = FirebaseOptions(
                applicationId = FirebaseConfig.appId,
                apiKey = FirebaseConfig.apiKey,
                projectId = FirebaseConfig.projectId,
                gcmSenderId = FirebaseConfig.messagingSenderId,
                storageBucket = FirebaseConfig.storageBucket,
                databaseUrl = null
            )
            Firebase.initialize(options)
            
            // Sign in with admin credentials
            try {
                Firebase.auth.signInWithEmailAndPassword(
                    FirebaseConfig.adminEmail,
                    FirebaseConfig.adminPassword
                )
                println("Admin authentication successful")
            } catch (e: Exception) {
                println("Admin authentication failed: ${e.message}")
                e.printStackTrace()
            }
            
            println("Firebase initialized successfully")
        } catch (e: Exception) {
            println("Firebase initialization failed: ${e.message}")
            e.printStackTrace()
        }
    }
} 