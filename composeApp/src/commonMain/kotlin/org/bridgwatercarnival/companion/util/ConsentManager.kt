package org.bridgwatercarnival.companion.util

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object ConsentManager {
    private const val CONSENT_KEY = "device_id_consent"
    private const val DEVICE_ID_KEY = "device_id"
    private val platform = getPlatformInstance()

    var hasConsent by mutableStateOf(false)
        private set

    var deviceId by mutableStateOf<String?>(null)
        private set

    init {
        // Load consent state and device ID from storage
        hasConsent = platform.getBoolean(CONSENT_KEY, false)
        if (hasConsent) {
            deviceId = platform.getString(DEVICE_ID_KEY, null)
        }
    }

    fun acceptConsent() {
        hasConsent = true
        platform.saveBoolean(CONSENT_KEY, true)
        // Generate and save device ID
        deviceId = platform.getDeviceId()
        platform.saveString(DEVICE_ID_KEY, deviceId)
    }

    fun declineConsent() {
        hasConsent = false
        deviceId = null
        platform.saveBoolean(CONSENT_KEY, false)
        platform.saveString(DEVICE_ID_KEY, null)
    }
} 