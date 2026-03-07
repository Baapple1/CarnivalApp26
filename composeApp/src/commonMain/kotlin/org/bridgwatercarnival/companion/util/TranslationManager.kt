package org.bridgwatercarnival.companion.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.bridgwatercarnival.companion.util.translations.Translations

/**
 * The main translation manager that handles all text translations in the app.
 * This is a singleton object that maintains the current language and translation maps.
 */
object TranslationManager {
    // Current language code (e.g., "en", "fr", "es")
    var currentLanguage: String by mutableStateOf("en")

    // List of supported languages
    private val supportedLanguages = listOf("en", "fr", "es", "de", "it")

    fun setLanguage(language: String) {
        // If the language is supported, use it directly
        if (supportedLanguages.contains(language)) {
            currentLanguage = language
            forceUpdate() // Force UI update when language changes
            return
        }

        // If the language is not supported, try to find a fallback
        val fallbackLanguage = when (language) {
            "fr" -> "fr"
            "es" -> "es"
            "de" -> "de"
            "it" -> "it"
            else -> "en" // Default to English for all other languages
        }
        currentLanguage = fallbackLanguage
        forceUpdate() // Force UI update when language changes
    }

    /**
     * Translates a key to the current language's text.
     * Falls back to English if the translation is missing.
     *
     * @param key The translation key to look up
     * @return The translated text, or the key itself if no translation is found
     */
    fun translate(key: String): String {
        return Translations.getTranslation(key, currentLanguage)
    }

    /**
     * Gets a list of all available language codes.
     *
     * @return List of language codes that have translations
     */
    fun getAvailableLanguages(): List<String> {
        return supportedLanguages
    }

    /**
     * Forces a UI update by triggering a recomposition.
     */
    fun forceUpdate() {
        val temp = currentLanguage
        currentLanguage = "temp"
        currentLanguage = temp
    }
}

/**
 * A CompositionLocal that provides access to the TranslationManager throughout the app.
 * This allows any composable to access the current translations.
 */
val LocalTranslationManager = staticCompositionLocalOf { TranslationManager }

/**
 * Creates a remembered instance of the TranslationManager.
 * This ensures the same instance is used throughout the composition.
 *
 * @return The TranslationManager instance
 */
@Composable
fun rememberTranslationManager(): TranslationManager {
    return remember { TranslationManager }
} 