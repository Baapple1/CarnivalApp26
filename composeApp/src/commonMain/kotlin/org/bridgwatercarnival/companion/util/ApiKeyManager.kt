package org.bridgwatercarnival.companion.util

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer
import okio.use

/**
 * Utility class to handle API keys and secrets securely.
 * This class loads keys from a properties file that is not committed to version control.
 */
object ApiKeyManager {
    private var properties: Map<String, String> = emptyMap()

    init {
        loadProperties()
    }

    private fun loadProperties() {
        try {
            val platform = getPlatformInstance()
            val path = platform.secretsPath.toPath()
            
            if (FileSystem.SYSTEM.exists(path)) {
                val fileContent = FileSystem.SYSTEM.source(path).buffer().use { source ->
                    source.readUtf8()
                }
                
                properties = fileContent
                    .split("\n")
                    .filter { it.isNotBlank() && !it.startsWith("#") }
                    .map { line ->
                        val parts = line.split("=", limit = 2)
                        if (parts.size == 2) {
                            parts[0].trim() to parts[1].trim()
                        } else null
                    }
                    .filterNotNull()
                    .toMap()
            } else {
                println("Warning: secrets.properties file not found. Using default empty values.")
                // If secrets.properties doesn't exist, use default values
                properties = mapOf(
                    "FIREBASE_API_KEY" to "",
                    "FIREBASE_AUTH_DOMAIN" to "",
                    "FIREBASE_PROJECT_ID" to "",
                    "FIREBASE_STORAGE_BUCKET" to "",
                    "FIREBASE_MESSAGING_SENDER_ID" to "",
                    "FIREBASE_APP_ID" to "",
                    "ADMIN_EMAIL" to "",
                    "ADMIN_PASSWORD" to "",
                    "MAPS_API_KEY" to "",
                    "WEATHER_API_KEY" to ""
                )
            }
        } catch (e: Exception) {
            println("Error loading API keys: ${e.message}")
            // Use empty values as fallback
            properties = emptyMap()
        }
    }

    /**
     * Get an API key by its name.
     * Returns an empty string if the key is not found.
     */
    fun getApiKey(keyName: String): String {
        return properties[keyName] ?: ""
    }

    /**
     * Check if an API key exists and is not empty.
     */
    fun hasApiKey(keyName: String): Boolean {
        return !getApiKey(keyName).isBlank()
    }

    /**
     * Check if all required API keys are present.
     */
    fun hasAllRequiredKeys(): Boolean {
        val requiredKeys = listOf(
            "FIREBASE_API_KEY",
            "FIREBASE_AUTH_DOMAIN",
            "FIREBASE_PROJECT_ID",
            "FIREBASE_STORAGE_BUCKET",
            "FIREBASE_MESSAGING_SENDER_ID",
            "FIREBASE_APP_ID"
        )
        return requiredKeys.all { hasApiKey(it) }
    }
} 