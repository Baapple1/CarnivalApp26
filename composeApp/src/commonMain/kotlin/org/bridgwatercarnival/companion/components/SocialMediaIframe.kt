package org.bridgwatercarnival.companion.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SocialMediaIframe(
    url: String,
    width: Int = 400,
    height: Int = 500,
    modifier: Modifier = Modifier,
    key: Any? = null
)

@Composable
fun SocialMediaIframeCommon(
    url: String,
    width: Int = 400,
    height: Int = 500,
    modifier: Modifier = Modifier,
    key: Any? = null
) {
    SocialMediaIframe(url, width, height, modifier, key)
} 