package org.bridgwatercarnival.companion.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.bridgwatercarnival.companion.util.Storage.initialize

class AndroidStorageInitializer : StorageInitializer {
    private lateinit var context: Context

    fun setContext(context: Context) {
        this.context = context
    }

    override fun initialize() {
        val storageManager = AndroidStorageManager()
        storageManager.initialize(context)
        initialize(storageManager)
    }
}

@Composable
actual fun getStorageInitializer(): StorageInitializer {
    val context = LocalContext.current
    return AndroidStorageInitializer().apply {
        setContext(context)
    }
} 