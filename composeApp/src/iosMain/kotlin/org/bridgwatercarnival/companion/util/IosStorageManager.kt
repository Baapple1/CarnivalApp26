package org.bridgwatercarnival.companion.util

import platform.Foundation.*
import platform.UIKit.*

class IosStorageManager : StorageManager {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun saveString(key: String, value: String) {
        defaults.setObject(value, key)
    }

    override fun getString(key: String, defaultValue: String): String {
        return defaults.stringForKey(key) ?: defaultValue
    }

    override fun saveBoolean(key: String, value: Boolean) {
        defaults.setBool(value, key)
        defaults.synchronize()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return defaults.boolForKey(key) ?: defaultValue
    }
}

fun initializeIosStorage() {
    Storage.initialize(IosStorageManager())
} 