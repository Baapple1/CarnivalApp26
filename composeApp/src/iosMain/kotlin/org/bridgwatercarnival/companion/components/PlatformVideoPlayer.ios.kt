package org.bridgwatercarnival.companion.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import org.bridgwatercarnival.companion.util.VideoManager
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURL
import platform.UIKit.UIColor
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.javaScriptEnabled
import platform.darwin.NSObject
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGRectZero

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformVideoPlayer(
    videoId: String,
    modifier: Modifier,
    onLoadingStateChanged: (Boolean) -> Unit
) {
    val htmlContent = remember(videoId) { VideoManager.getEmbeddedVideoHtml(videoId) }
    
    // Create a navigation delegate to handle loading states
    class WebViewDelegate : NSObject(), WKNavigationDelegateProtocol {
        var onFinished: () -> Unit = {}
        
        override fun webView(webView: WKWebView, didFinishNavigation: platform.WebKit.WKNavigation?) {
            onFinished()
            
            // Fix video display with JavaScript
            webView.evaluateJavaScript("""
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
        
        override fun webView(webView: WKWebView, didFailNavigation: platform.WebKit.WKNavigation?, withError: platform.Foundation.NSError) {
            onFinished()
        }
    }
    
    val delegate = remember { WebViewDelegate() }
    delegate.onFinished = { onLoadingStateChanged(false) }
    
    UIKitView(
        modifier = modifier,
        factory = {
            val config = WKWebViewConfiguration().apply {
                preferences.apply {
                    javaScriptEnabled = true
                }
                allowsInlineMediaPlayback = true
                mediaTypesRequiringUserActionForPlayback = 0UL // No user action needed for autoplay
            }
            
            WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = config).apply {
                navigationDelegate = delegate
                backgroundColor = UIColor.blackColor
                
                // Allow video to play
                setAllowsBackForwardNavigationGestures(true)
            }
        },
        update = { webView ->
            onLoadingStateChanged(true)
            
            webView.loadHTMLString(htmlContent, NSURL(string = "https://www.youtube.com"))
        }
    )
    
    // Clean up resources
    DisposableEffect(Unit) {
        onDispose {
            // Clean up if needed
        }
    }
} 