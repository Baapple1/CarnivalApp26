package org.bridgwatercarnival.companion.util

import android.content.Context
import android.content.res.Configuration
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.app.Application
import android.content.ContextWrapper

actual class Platform {
    actual val platform: String = "Android"
    actual val language: String = "en" // Default to English for Android
    actual val secretsPath: String = "secrets.properties"

    private val context: Context
        get() = (Application().applicationContext as ContextWrapper).baseContext

    @Composable
    actual fun getStorage(): Any {
        return LocalContext.current.getSharedPreferences("app_storage", Context.MODE_PRIVATE)
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return context.getSharedPreferences("app_storage", Context.MODE_PRIVATE)
            .getBoolean(key, defaultValue)
    }

    actual fun saveBoolean(key: String, value: Boolean) {
        context.getSharedPreferences("app_storage", Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    actual fun getString(key: String, defaultValue: String?): String? {
        return context.getSharedPreferences("app_storage", Context.MODE_PRIVATE)
            .getString(key, defaultValue)
    }

    actual fun saveString(key: String, value: String?) {
        context.getSharedPreferences("app_storage", Context.MODE_PRIVATE)
            .edit()
            .putString(key, value)
            .apply()
    }

    actual fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: ""
    }
}

@Composable
actual fun getPlatform(): Platform {
    return Platform()
}

actual fun getPlatformInstance(): Platform = Platform()

actual object PlatformInfo {
    actual val isIOS: Boolean = false
} 