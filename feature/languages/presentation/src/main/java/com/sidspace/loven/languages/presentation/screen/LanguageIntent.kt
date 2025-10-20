package com.sidspace.loven.languages.presentation.screen

sealed interface LanguageIntent {
    fun selectLanguage(idLanguage: String): LanguageIntent
}
