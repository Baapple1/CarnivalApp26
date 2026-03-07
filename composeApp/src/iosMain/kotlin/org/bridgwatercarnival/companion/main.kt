package org.bridgwatercarnival.companion

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.*
import kotlinx.cinterop.ExperimentalForeignApi
import platform.objc.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalForeignApi::class)
fun createMainViewController(): UIViewController = ComposeUIViewController { 
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = getTopPadding(), bottom = 0.dp, start = 0.dp, end = 0.dp)
    ) {
        App()
    }
}.apply {
    // Configure view controller for edge-to-edge layout
    edgesForExtendedLayout = UIRectEdgeAll
    extendedLayoutIncludesOpaqueBars = true

    // Set up view for edge-to-edge display
    view.backgroundColor = UIColor.blackColor

    // Make status bar light (white) for better visibility
    modalPresentationCapturesStatusBarAppearance = true
    overrideUserInterfaceStyle = UIUserInterfaceStyle.UIUserInterfaceStyleDark

    // Configure status bar appearance
    setNeedsStatusBarAppearanceUpdate()

    // Disable safe area insets
    view.clipsToBounds = false
    view.setNeedsLayout()
    additionalSafeAreaInsets = UIEdgeInsetsMake(0.0, 0.0, 0.0, 0.0)
}