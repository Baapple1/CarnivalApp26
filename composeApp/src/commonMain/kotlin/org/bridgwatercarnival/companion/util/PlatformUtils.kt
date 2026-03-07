package org.bridgwatercarnival.companion.util

import androidx.compose.runtime.Composable

@Composable
fun getCurrentPlatformType(): PlatformType {
    val platform = getPlatform()
    return when (platform.platform) {
        "Android" -> PlatformType.ANDROID
        "iOS" -> PlatformType.IOS
        else -> PlatformType.UNKNOWN
    }
} 