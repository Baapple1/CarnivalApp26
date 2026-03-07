package org.bridgwatercarnival.companion.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LanguageManager {
    private val _currentLanguage = MutableStateFlow(loadSavedLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private fun loadSavedLanguage(): String {
        return Storage.getString(StorageKeys.LANGUAGE_PREFERENCE, "en")
    }

    fun setLanguage(language: String) {
        _currentLanguage.value = language
        Storage.saveString(StorageKeys.LANGUAGE_PREFERENCE, language)
    }

    fun getCurrentLanguage(): String = _currentLanguage.value

    // String resources for different languages
    private val strings = mapOf(
        "en" to mapOf(
            "settings" to "Settings",
            "language" to "Language",
            "english" to "English",
            "french" to "French",
            "spanish" to "Spanish"
        ),
        "fr" to mapOf(
            "settings" to "Paramètres",
            "language" to "Langue",
            "english" to "Anglais",
            "french" to "Français",
            "spanish" to "Espagnol"
        ),
        "es" to mapOf(
            "settings" to "Ajustes",
            "language" to "Idioma",
            "english" to "Inglés",
            "french" to "Francés",
            "spanish" to "Español"
        )
    )

    fun getString(key: String): String {
        val language = getCurrentLanguage()
        return strings[language]?.get(key) ?: strings["en"]?.get(key) ?: key
    }
} 