package org.bridgwatercarnival.companion.util

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getUriHandler(): UriHandler {
    val context = LocalContext.current
    return object : UriHandler {
        override fun openUri(uri: String) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse(uri)
            }
            context.startActivity(intent)
        }
    }
} 