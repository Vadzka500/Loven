package com.sidspace.loven.lessons.presentation.screen

sealed class LessonsEffect {
    data class ToGame(val idLanguage: String, val idModule: String, val idLesson: String) : LessonsEffect()
}
