package org.bridgwatercarnival.companion.util


import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun getUriHandler(): UriHandler {
    return object : UriHandler {
        override fun openUri(uri: String) {
            val nsUrl = NSURL.URLWithString(uri)
            println("[UriHandler] Attempting to open URL: $uri (NSURL: $nsUrl)")
            if (nsUrl != null) {
                dispatch_async(dispatch_get_main_queue()) {
                    if (UIApplication.sharedApplication.canOpenURL(nsUrl)) {
                        UIApplication.sharedApplication.openURL(nsUrl, options = emptyMap<Any?, Any>(), completionHandler = null)
                        println("[UriHandler] openURL called with options.")
                    } else {
                        println("[UriHandler] Cannot open URL: $uri")
                    }
                }
            } else {
                println("[UriHandler] Invalid NSURL, cannot open.")
            }
        }
    }
}
