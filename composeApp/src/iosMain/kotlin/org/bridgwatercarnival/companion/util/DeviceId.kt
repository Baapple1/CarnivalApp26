package org.bridgwatercarnival.companion.util

import kotlinx.datetime.Clock
import platform.UIKit.UIDevice
import platform.Foundation.NSUserDefaults

actual fun getDeviceId(): String {
    val defaults = NSUserDefaults.standardUserDefaults
    val storedId = defaults.stringForKey("carnival_device_id")
    
    if (storedId != null) {
        return storedId
    }
    
    val vendorId = UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "unknown"
    val newId = "$vendorId-${Clock.System.now().toEpochMilliseconds()}"
    defaults.setObject(newId, "carnival_device_id")
    return newId
} 