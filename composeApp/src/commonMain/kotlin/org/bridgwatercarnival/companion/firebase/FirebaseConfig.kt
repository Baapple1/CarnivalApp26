package org.bridgwatercarnival.companion.firebase

import org.bridgwatercarnival.companion.util.ApiKeyManager

object FirebaseConfig {
    val projectId: String = ApiKeyManager.getApiKey("FIREBASE_PROJECT_ID")
    val apiKey: String = ApiKeyManager.getApiKey("FIREBASE_API_KEY")
    val appId: String = ApiKeyManager.getApiKey("FIREBASE_APP_ID")
    val authDomain: String = ApiKeyManager.getApiKey("FIREBASE_AUTH_DOMAIN")
    val storageBucket: String = ApiKeyManager.getApiKey("FIREBASE_STORAGE_BUCKET")
    val messagingSenderId: String = ApiKeyManager.getApiKey("FIREBASE_MESSAGING_SENDER_ID")
    
    // Admin credentials
    val adminEmail: String = ApiKeyManager.getApiKey("ADMIN_EMAIL")
    val adminPassword: String = ApiKeyManager.getApiKey("ADMIN_PASSWORD")
} 