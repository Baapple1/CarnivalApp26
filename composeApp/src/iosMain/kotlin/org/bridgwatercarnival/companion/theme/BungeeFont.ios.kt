package org.bridgwatercarnival.companion.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.UIKit.UIFont
import platform.posix.memcpy

// Debug: Print available font names
private fun printAvailableFonts() {
    UIFont.familyNames.forEach { familyName ->
        println("Font family: $familyName")
        UIFont.fontNamesForFamilyName(familyName as String)?.forEach { fontName ->
            println("- Font name: $fontName")
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual val bungeeFont: FontFamily = FontFamily(
    Font(
        "Bungee",
        { // Load font data using a lambda
            NSBundle.mainBundle.pathForResource(
                "Bungee-Regular",
                "ttf"
            )?.let { path ->
                NSData.dataWithContentsOfFile(path)?.let { nsData ->
                    ByteArray(nsData.length.toInt()).apply {
                        usePinned { pinnedArray ->
                            memcpy(
                                pinnedArray.addressOf(0),
                                nsData.bytes,
                                nsData.length.toULong()
                            )
                        }
                    }
                }
            } ?: ByteArray(0)
        },
        FontWeight.Normal,
        FontStyle.Normal
    )
).also {
    // Print available fonts when this is initialized
    printAvailableFonts()
}
