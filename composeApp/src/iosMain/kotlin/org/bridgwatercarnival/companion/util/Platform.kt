package org.bridgwatercarnival.companion.util

import androidx.compose.runtime.Composable
import platform.Foundation.*
import platform.UIKit.*

actual class Platform {
    actual val platform: String = "iOS"
    actual val language: String
        get() = NSLocale.currentLocale.languageCode ?: "en"
    actual val secretsPath: String
        get() = NSHomeDirectory() + "/Documents/secrets.json"

    private val defaults = NSUserDefaults.standardUserDefaults

    @Composable
    actual fun getStorage(): Any = defaults

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        defaults.boolForKey(key) ?: defaultValue

    actual fun saveBoolean(key: String, value: Boolean) {
        defaults.setBool(value, key)
        defaults.synchronize()
    }

    actual fun getString(key: String, defaultValue: String?): String? =
        defaults.stringForKey(key) ?: defaultValue

    actual fun saveString(key: String, value: String?) {
        if (value != null) {
            defaults.setObject(value, key)
        } else {
            defaults.removeObjectForKey(key)
        }
        defaults.synchronize()
    }

    actual fun getDeviceId(): String {
        val key = "device_id"
        val existingId = getString(key, null)
        if (existingId != null) return existingId

        val newId = NSUUID.UUID().UUIDString
        saveString(key, newId)
        return newId
    }
}

@Composable
actual fun getPlatform(): Platform = Platform()

actual fun getPlatformInstance(): Platform = Platform()

actual object PlatformInfo {
    actual val isIOS: Boolean = true
} 
