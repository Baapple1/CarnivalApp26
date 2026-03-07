package org.bridgwatercarnival.companion.components

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceError
import android.webkit.WebSettings
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.jetbrains.compose.resources.painterResource
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.CloudOff
import org.bridgwatercarnival.companion.util.TranslationManager

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
                
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
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
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        // Enable JavaScript
                        javaScriptEnabled = true
                        
                        // Enable DOM storage
                        domStorageEnabled = true
                        
                        // Enable zooming
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        
                        // Set default cache mode
                        cacheMode = WebSettings.LOAD_DEFAULT
                        
                        // Allow mixed content
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
    
                    // WebViewClient with error handling
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            // Allow all navigation
                            return false
                        }
                        
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            // Reset error state when loading starts
                            hasError = false
                            errorMessage = ""
                        }
                        
                        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                            super.onReceivedError(view, request, error)
                            // Always show error message for any error
                            hasError = true
                            errorMessage = "Error: ${error?.description}"
                            
                            // Force the WebView to be hidden and show our error UI
                            view?.visibility = android.view.View.GONE
                        }
                        
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            
                            // Only run on Bridgwater Carnival website
                            if (url?.contains("bridgwatercarnival.org.uk") == true) {
                                // Simple script to accept cookies and ensure links work
                                view?.evaluateJavascript("""
                                    (function() {
                                        // Handle cookie consent buttons
                                        function acceptCookies() {
                                            // Try to find and click accept buttons
                                            var buttons = document.querySelectorAll('button, .btn, input[type="button"]');
                                            for (var i = 0; i < buttons.length; i++) {
                                                var btn = buttons[i];
                                                var text = btn.innerText || btn.value || '';
                                                if (text.toLowerCase().includes('accept') || 
                                                    text.toLowerCase().includes('agree') || 
                                                    text.toLowerCase().includes('allow') || 
                                                    text.toLowerCase().includes('continue')) {
                                                    btn.click();
                                                    console.log('Clicked cookie button:', text);
                                                }
                                            }
                                        }
                                        
                                        // Try immediately
                                        acceptCookies();
                                        
                                        // Try again after a delay
                                        setTimeout(acceptCookies, 1000);
                                    })();
                                """.trimIndent(), null)
                            }
                        }
                    }
    
                    // Load the URL
                    loadUrl(url)
                }
            },
            update = { webView ->
                // Only update URL if it changed and there's no error
                if (!hasError && webView.url != url) {
                    webView.loadUrl(url)
                }
            },
            modifier = modifier
        )
    }
} 