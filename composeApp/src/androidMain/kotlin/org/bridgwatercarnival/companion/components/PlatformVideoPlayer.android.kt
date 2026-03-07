package org.bridgwatercarnival.companion.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.bridgwatercarnival.companion.util.VideoManager

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun PlatformVideoPlayer(
    videoId: String,
    modifier: Modifier,
    onLoadingStateChanged: (Boolean) -> Unit
) {
    val htmlContent = remember(videoId) { VideoManager.getEmbeddedVideoHtml(videoId) }
    
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                
                settings.apply {
                    javaScriptEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    builtInZoomControls = false
                    displayZoomControls = false
                }
                
                webChromeClient = WebChromeClient()
                
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)
                        onLoadingStateChanged(false)
                    }
                    
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        onLoadingStateChanged(false)
                    }
                }
            }
        },
        update = { webView ->
            onLoadingStateChanged(true)
            webView.loadDataWithBaseURL(
                "https://www.youtube.com",
                htmlContent,
                "text/html",
                "UTF-8",
                null
            )
            
            // Add JavaScript to make everything clickable and ensure video displays properly
            webView.evaluateJavascript("""
                (function() {
                    // Make sure all elements can be clicked
                    var style = document.createElement('style');
                    style.textContent = '* { pointer-events: auto !important; }';
                    document.head.appendChild(style);
                    
                    // Fix video display
                    var videoElements = document.getElementsByTagName('iframe');
                    for (var i = 0; i < videoElements.length; i++) {
                        videoElements[i].style.width = '100%';
                        videoElements[i].style.height = '100%';
                        videoElements[i].style.position = 'absolute';
                        videoElements[i].style.top = '0';
                        videoElements[i].style.left = '0';
                    }
                })();
            """.trimIndent(), null)
        }
    )
    
    // Clean up resources when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            // Clean up logic if needed
        }
    }
} 