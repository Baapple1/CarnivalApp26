package org.bridgwatercarnival.companion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.CloudOff
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import org.bridgwatercarnival.companion.util.TranslationManager
import org.jetbrains.compose.resources.painterResource
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.setJavaScriptEnabled
import platform.darwin.NSObject
import platform.Foundation.NSTimer

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun SocialMediaIframe(
    url: String,
    width: Int,
    height: Int,
    modifier: Modifier,
    key: Any?
) {
    // Track loading state and errors
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (hasError) {
        // Display error message when there's a connection problem
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.CloudOff),
                    contentDescription = TranslationManager.translate("no_internet_connection"),
                    modifier = Modifier.size(64.dp),
                    tint = Color.Red
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = TranslationManager.translate("internet_connection_required"),
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = TranslationManager.translate("check_internet_connection"),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        }
    } else {
        UIKitView(
            factory = {
                // Basic configuration with minimal settings
                val config = WKWebViewConfiguration().apply {
                    // Enable JavaScript
                    preferences.setJavaScriptEnabled(true)
                }

                // Create a simple WebView
                val webView = WKWebView(
                    frame = CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()),
                    configuration = config
                ).apply {
                    // Enable basic interaction
                    setUserInteractionEnabled(true)
                    scrollView.setScrollEnabled(true)
                    setAllowsBackForwardNavigationGestures(true)
                }

                // Periodically inject pointer-events fix for SPA/dynamic sites
                NSTimer.scheduledTimerWithTimeInterval(1.0, repeats = true) { _ ->
                    webView.evaluateJavaScript(
                        """
                        (function() {
                            if (!document.getElementById('pointer-events-fix')) {
                                var style = document.createElement('style');
                                style.id = 'pointer-events-fix';
                                style.textContent = '* { pointer-events: auto !important; }';
                                document.head.appendChild(style);
                            }
                        })();
                        """, null
                    )
                }

                // Set up a very basic navigation delegate
                val navigationDelegate = object : NSObject(), WKNavigationDelegateProtocol {
                    // Helper to inject pointer-events fix
                    private fun injectPointerEventsFix(webView: WKWebView) {
                        webView.evaluateJavaScript(
                            """
                            (function() {
                                var style = document.createElement('style');
                                style.textContent = '* { pointer-events: auto !important; }';
                                document.head.appendChild(style);
                            })();
                            """, null
                        )
                    }

                    override fun webView(
                        webView: WKWebView,
                        decidePolicyForNavigationAction: WKNavigationAction,
                        decisionHandler: (WKNavigationActionPolicy) -> Unit
                    ) {
                        // If the navigation action would open a new window (target=_blank), open in Safari
                        if (decidePolicyForNavigationAction.targetFrame?.mainFrame == false) {
                            val url = decidePolicyForNavigationAction.request.URL
                            if (url != null) {
                                platform.UIKit.UIApplication.sharedApplication.openURL(url)
                                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                                return
                            }
                        }
                        // Always allow navigation otherwise
                        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
                    }

                    // Handle navigation failures
                    @ObjCSignatureOverride
                    override fun webView(webView: WKWebView, didFailProvisionalNavigation: WKNavigation?, withError: NSError) {
                        hasError = true
                        errorMessage = withError.localizedDescription ?: "Connection error"
                    }

                    // Handle loading failures
                    @ObjCSignatureOverride
                    override fun webView(webView: WKWebView, didFailNavigation: WKNavigation?, withError: NSError) {
                        hasError = true
                        errorMessage = withError.localizedDescription ?: "Connection error"
                    }

                    // Explicitly check for errors on start
                    override fun webView(webView: WKWebView, didStartProvisionalNavigation: WKNavigation?) {
                        // Check network status if possible
                        val urlString = webView.URL?.absoluteString ?: ""
                        if (urlString.isEmpty()) {
                            hasError = true
                            errorMessage = "Failed to load URL"
                        }
                    }

                    override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
                        hasError = false
                        errorMessage = ""
                        injectPointerEventsFix(webView)

                        val urlString = webView.URL?.absoluteString ?: ""
                        if (urlString.contains("bridgwatercarnival.org.uk")) {
                            // Very simple cookie acceptance script
                            webView.evaluateJavaScript("""
                                (function() {
                                    // Try to accept cookies
                                    function acceptCookies() {
                                        var buttons = document.querySelectorAll('button');
                                        for (var i = 0; i < buttons.length; i++) {
                                            var btn = buttons[i];
                                            var text = btn.innerText || '';
                                            if (text.toLowerCase().includes('accept') || 
                                               text.toLowerCase().includes('agree')) {
                                                btn.click();
                                            }
                                        }
                                    }
                                    setTimeout(acceptCookies, 1000);
                                })();
                            """, null)
                        }
                    }

                    override fun webView(webView: WKWebView, didCommitNavigation: WKNavigation?) {
                        injectPointerEventsFix(webView)
                    }
                }

                // Set the navigation delegate
                webView.navigationDelegate = navigationDelegate

                // Load the URL
                NSURL.URLWithString(url)?.let { nsUrl ->
                    webView.loadRequest(NSURLRequest.requestWithURL(nsUrl))
                }

                webView
            },
            update = { webView ->
                // Only update URL if it changed
                val currentUrl = webView.URL?.absoluteString
                if (currentUrl != url) {
                    NSURL.URLWithString(url)?.let { nsUrl ->
                        webView.loadRequest(NSURLRequest.requestWithURL(nsUrl))
                    }
                }
            },
            modifier = modifier
        )
    }
} 
