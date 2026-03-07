package org.bridgwatercarnival.companion.util

import androidx.compose.runtime.Composable

interface StorageInitializer {
    fun initialize()
}

@Composable
expect fun getStorageInitializer(): StorageInitializer 