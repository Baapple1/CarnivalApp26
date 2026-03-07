package org.bridgwatercarnival.companion

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.configureSystemUI() {
    // Make content appear behind the system bars
    WindowCompat.setDecorFitsSystemWindows(window, false)
    
    // Make the status bar and navigation bar transparent but maintain color
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    
    // Ensure system bars stay visible
    WindowInsetsControllerCompat(window, window.decorView).apply {
        show(WindowInsetsCompat.Type.systemBars())
        isAppearanceLightStatusBars = false  // Use light (white) icons
        isAppearanceLightNavigationBars = false  // Use light (white) icons
    }
}