package com.sidspace.loven.languages.presentation.screen

sealed interface LanguageIntent {
    data class SelectLanguage(val idLanguage: String): LanguageIntent
}
