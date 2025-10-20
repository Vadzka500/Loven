package com.sidspace.loven.lessons.presentation.screen

sealed interface LessonsIntent {
    data class SelectLesson(val idLanguage: String, val idModule: String, val idLesson: String) : LessonsIntent
    object OnHideDialog : LessonsIntent
}
