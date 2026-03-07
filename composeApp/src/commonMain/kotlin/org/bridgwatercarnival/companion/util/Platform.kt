package org.bridgwatercarnival.companion.util

import androidx.compose.runtime.Composable

expect class Platform {
    @Composable
    fun getStorage(): Any
    
    // Non-composable storage methods
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun saveBoolean(key: String, value: Boolean)
    fun getString(key: String, defaultValue: String?): String?
    fun saveString(key: String, value: String?)
    
    val platform: String
    val language: String
    val secretsPath: String
    
    fun getDeviceId(): String
}

@Composable
expect fun getPlatform(): Platform

// Non-composable way to get platform
expect fun getPlatformInstance(): Platform

enum class PlatformType {
    ANDROID,
    IOS,
    UNKNOWN
}

expect object PlatformInfo {
    val isIOS: Boolean
} 