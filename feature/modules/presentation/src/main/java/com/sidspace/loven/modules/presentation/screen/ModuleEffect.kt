package com.sidspace.loven.modules.presentation.screen

sealed class ModuleEffect {
    data class ToLessonsScreen(val idLanguage: String, val idModule: String) : ModuleEffect()
}


