package org.bridgwatercarnival.companion.util.translations

/**
 * Enum class containing all translation keys used in the app.
 * This provides type-safe access to translations and helps prevent typos.
 */
enum class TranslationKey(val key: String) {
    // General
    EXPLORE("explore"),
    MAP("map"),
    STORE("store"),
    SETTINGS("settings"),
    CLOSE("close"),
    ENGLISH("english"),
    FRENCH("french"),
    SPANISH("spanish"),
    GERMAN("german"),
    ITALIAN("italian"),
    
    // Navigation
    BACK("back"),
    NEXT("next"),
    
    // Welcome Dialog
    WELCOME_TITLE("welcome_title"),
    WELCOME_SUBTITLE("welcome_subtitle"),
    CARNIVAL_COUNTDOWN("carnival_countdown"),
    FEATURES_TITLE("features_title"),
    START_PLANNING("start_planning"),
    
    // Features
    FEATURE_MAP("feature_map"),
    FEATURE_VOTING("feature_voting"),
    FEATURE_PARKING("feature_parking"),
    FEATURE_SCHEDULE("feature_schedule"),
    FEATURE_ENTERTAINMENT("feature_entertainment"),
    FEATURE_LOCATIONS("feature_locations"),
    
    // Voting
    VOTING_TITLE("voting_title"),
    VOTING_ALREADY_TITLE("voting_already_title"),
    VOTING_ALREADY_MESSAGE("voting_already_message"),
    VOTING_CHECK_RESULTS("voting_check_results"),
    VOTING_LOCKED_ICON("voting_locked_icon"),
    VOTING_NOT_AVAILABLE_TITLE("voting_not_available_title"),
    VOTING_AVAILABLE_LATER("voting_available_later"),
    VOTING_REFRESH_PAGE("voting_refresh_page"),
    VOTING_CLOSE("voting_close"),
    
    // Results
    RESULTS_TITLE("results_title"),
    RESULTS_NOT_AVAILABLE("results_not_available"),
    RESULTS_CHECK_BACK("results_check_back"),
    
    // Gallery
    GALLERY_TITLE("gallery_title"),
    GALLERY_2023_TITLE("gallery_2023_title"),
    GALLERY_2024_TITLE("gallery_2024_title"),
    GALLERY_2025_TITLE("gallery_2025_title"),
    
    // Error Messages
    NO_INTERNET_CONNECTION("no_internet_connection"),
    INTERNET_CONNECTION_REQUIRED("internet_connection_required"),
    CHECK_INTERNET_CONNECTION("check_internet_connection"),
    
    // Club Names
    RAMBLERS_TITLE("ramblers_title"),
    RAMBLERS_DESCRIPTION("ramblers_description"),
    MARKETEERS_TITLE("marketeers_title"),
    MARKETEERS_DESCRIPTION("marketeers_description"),
    GREMLINS_TITLE("gremlins_title"),
    GREMLINS_DESCRIPTION("gremlins_description"),
    LIME_KILN_TITLE("lime_kiln_title"),
    LIME_KILN_DESCRIPTION("lime_kiln_description"),
    GRIFFENS_TITLE("griffens_title"),
    GRIFFENS_DESCRIPTION("griffens_description"),
    VAGABONDS_TITLE("vagabonds_title"),
    VAGABONDS_DESCRIPTION("vagabonds_description"),
    BRITISH_FLAG_TITLE("british_flag_title"),
    BRITISH_FLAG_DESCRIPTION("british_flag_description"),
    RENEGADES_TITLE("renegades_title"),
    RENEGADES_DESCRIPTION("renegades_description"),
    CRUSADERS_TITLE("crusaders_title"),
    CRUSADERS_DESCRIPTION("crusaders_description"),
    CAVALIERS_TITLE("cavaliers_title"),
    CAVALIERS_DESCRIPTION("cavaliers_description");

    companion object {
        private val keyMap = values().associateBy { it.key }
        
        fun fromKey(key: String): TranslationKey? = keyMap[key]
    }
}

/**
 * Data class to hold translations for a specific language
 */
data class LanguageTranslations(
    val languageCode: String,
    val translations: Map<TranslationKey, String>
)

/**
 * Object containing all translations for all supported languages
 */
object Translations {
    val allTranslations: Map<String, Map<String, String>> = mapOf(
        "en" to EnglishTranslations.translations,
        "fr" to FrenchTranslations.translations,
        "es" to SpanishTranslations.translations,
        "de" to GermanTranslations.translations,
        "it" to ItalianTranslations.translations
    )

    /**
     * Get a translation for a specific key and language
     * Falls back to English if the translation is missing
     */
    fun getTranslation(key: String, language: String): String {
        // First try to get the translation in the requested language
        val translation = allTranslations[language]?.get(key)
        if (translation != null) {
            return translation
        }

        // If not found, try English as fallback
        val englishTranslation = allTranslations["en"]?.get(key)
        if (englishTranslation != null) {
            return englishTranslation
        }

        // If still not found, return the key itself
        return key
    }
} 