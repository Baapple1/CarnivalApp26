package org.bridgwatercarnival.companion.util

import android.os.Build
import java.util.UUID

actual fun getDeviceId(): String {
    val deviceId = Build.SERIAL ?: UUID.randomUUID().toString()
    val secretKey = EncryptionUtil.generateKey() // Generate or retrieve your secret key securely
    return EncryptionUtil.encrypt(deviceId, secretKey)
}