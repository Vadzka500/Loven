package com.sidspace.loven.languages.presentation.screen

sealed class LanguageEffect {
    data class ToModulesScreen(val idLanguage: String): LanguageEffect()
}
