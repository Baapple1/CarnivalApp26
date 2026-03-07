package org.bridgwatercarnival.companion.util

import android.os.Build
import java.util.UUID

actual fun getDeviceId(): String {
    return Build.SERIAL ?: UUID.randomUUID().toString()
} 