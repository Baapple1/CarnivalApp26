package org.bridgwatercarnival.companion

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.*

@OptIn(ExperimentalForeignApi::class)
fun configureSystemUI() {
    val keyWindow = UIApplication.sharedApplication.keyWindow
    
    keyWindow?.let { window ->
        // Make status bar transparent
        window.rootViewController?.setNeedsStatusBarAppearanceUpdate()
        
        // Set window to use full screen
        window.backgroundColor = UIColor.clearColor
        window.rootViewController?.edgesForExtendedLayout = UIRectEdgeAll
    }
} 