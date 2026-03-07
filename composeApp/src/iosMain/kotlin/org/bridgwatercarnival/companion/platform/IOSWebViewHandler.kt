package org.bridgwatercarnival.companion.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.WebKit.*
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.darwin.NSObject

actual fun getWebViewHandler(): WebViewHandler = IOSWebViewHandler()

class IOSWebViewHandler : WebViewHandler {
    @OptIn(ExperimentalForeignApi::class)
    @Composable
    override fun WebView(
        url: String,
        modifier: Modifier,
        onPageFinished: () -> Unit,
        onError: (String) -> Unit
    ) {
        UIKitView(
            factory = {
                val config = WKWebViewConfiguration().apply {
                    allowsInlineMediaPlayback = true
                    mediaTypesRequiringUserActionForPlayback = WKAudiovisualMediaTypeNone
                    websiteDataStore = WKWebsiteDataStore.defaultDataStore()
                    preferences.apply {
                        setJavaScriptEnabled(true)
                        setJavaScriptCanOpenWindowsAutomatically(true)
                    }
                }
                
                val frame = CGRectMake(0.0, 0.0, 0.0, 0.0)
                val webView = WKWebView(frame = frame, configuration = config).apply {
                    setCustomUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Safari/605.1.15")
                    setAllowsBackForwardNavigationGestures(true)
                    setAllowsLinkPreview(true)
                }
                
                webView.navigationDelegate = object : NSObject(), WKNavigationDelegateProtocol {
                    override fun webView(
                        webView: WKWebView,
                        didFinishNavigation: WKNavigation?
                    ) {
                        onPageFinished()
                    }

                    override fun webView(
                        webView: WKWebView,
                        didFailNavigation: WKNavigation?,
                        withError: platform.Foundation.NSError
                    ) {
                        onError(withError.localizedDescription)
                    }

                    override fun webView(
                        webView: WKWebView,
                        decidePolicyForNavigationAction: WKNavigationAction,
                        decisionHandler: (WKNavigationActionPolicy) -> Unit
                    ) {
                        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
                    }
                }
                
                NSURL.URLWithString(url)?.let { nsUrl ->
                    webView.loadRequest(NSURLRequest.requestWithURL(nsUrl))
                }
                webView
            },
            modifier = modifier
        )
    }
} 