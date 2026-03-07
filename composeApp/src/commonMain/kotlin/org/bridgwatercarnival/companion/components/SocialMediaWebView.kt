package org.bridgwatercarnival.companion.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SocialMediaWebView(url: String, modifier: Modifier = Modifier)

@Composable
fun SocialMediaWebViewCommon(url: String, modifier: Modifier = Modifier) {
    SocialMediaWebView(url, modifier)
} 