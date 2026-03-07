package org.bridgwatercarnival.companion.util

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

/**
 * A composable function that displays translated text.
 * This is the main component used throughout the app for displaying text in different languages.
 *
 * @param key The translation key to look up in the current language's translation map
 * @param style The text style to apply (font, size, color, etc.)
 * @param textAlign Optional text alignment (left, center, right)
 * @param modifier Optional modifier for additional styling or layout adjustments
 */
@Composable
fun TranslatedText(
    key: String,
    style: TextStyle = TextStyle.Default,
    textAlign: TextAlign? = null,
    modifier: Modifier = Modifier
) {
    // Get the current TranslationManager from the CompositionLocal
    val translationManager = LocalTranslationManager.current
    // Create a mutable state to force recomposition when language changes
    var text by remember { mutableStateOf(translationManager.translate(key)) }
    
    // Update text when language changes
    LaunchedEffect(translationManager.currentLanguage) {
        text = translationManager.translate(key)
    }
    
    // Display the translated text with the specified style and alignment
    Text(
        text = text,
        style = style,
        textAlign = textAlign,
        modifier = modifier
    )
}

/**
 * A composable function that provides the TranslationManager to its children.
 * This should wrap the root of your app to make translations available throughout.
 *
 * @param content The composable content that needs access to translations
 */
@Composable
fun TranslationProvider(
    content: @Composable () -> Unit
) {
    // Provide the TranslationManager to all child composables
    CompositionLocalProvider(LocalTranslationManager provides rememberTranslationManager()) {
        content()
    }
} 