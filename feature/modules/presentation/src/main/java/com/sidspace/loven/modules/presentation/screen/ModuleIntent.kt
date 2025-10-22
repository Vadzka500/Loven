package com.sidspace.loven.modules.presentation.screen

sealed interface ModuleIntent {
    data class ToLessonsScreen(val idLanguage: String, val idModule: String) : ModuleIntent
}
