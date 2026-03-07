package org.bridgwatercarnival.companion.util

import androidx.compose.runtime.Composable

class IosStorageInitializer : StorageInitializer {
    override fun initialize() {
        Storage.initialize(IosStorageManager())
    }
}

@Composable
actual fun getStorageInitializer(): StorageInitializer {
    return IosStorageInitializer()
} 