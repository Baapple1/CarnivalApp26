package org.bridgwatercarnival.companion.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface WebViewHandler {
    @Composable
    fun WebView(
        url: String,
        modifier: Modifier = Modifier,
        onPageFinished: () -> Unit = {},
        onError: (String) -> Unit = {}
    )
}

expect fun getWebViewHandler(): WebViewHandler 