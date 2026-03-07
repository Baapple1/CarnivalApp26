package org.bridgwatercarnival.companion.util

import androidx.compose.runtime.Composable

@Composable
expect fun getUriHandler(): UriHandler

interface UriHandler {
    fun openUri(uri: String)
} 