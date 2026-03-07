package org.bridgwatercarnival.companion.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler

@Composable
actual fun getUriHandler(): UriHandler {
    val uriHandler = LocalUriHandler.current
    return object : UriHandler {
        override fun openUri(uri: String) {
            uriHandler.openUri(uri)
        }
    }
} 