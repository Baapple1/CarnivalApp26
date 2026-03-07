package org.bridgwatercarnival.companion.util

object Storage {
    private var storageManager: StorageManager? = null

    fun initialize(manager: StorageManager) {
        storageManager = manager
    }

    fun saveString(key: String, value: String) {
        storageManager?.saveString(key, value)
    }

    fun getString(key: String, defaultValue: String): String {
        return storageManager?.getString(key, defaultValue) ?: defaultValue
    }

    fun saveBoolean(key: String, value: Boolean) {
        storageManager?.saveString(key, value.toString())
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return storageManager?.getString(key, defaultValue.toString())?.toBoolean() ?: defaultValue
    }
} 