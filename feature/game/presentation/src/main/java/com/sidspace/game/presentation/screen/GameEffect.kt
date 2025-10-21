package com.sidspace.game.presentation.screen

sealed class GameEffect {
    data class CorrectWords(val wordRu: String, val wordTranslate: String) : GameEffect()
    data class InCorrectWords(val wordRu: String, val wordTranslate: String) : GameEffect()
    data object ToLessons: GameEffect()
    object Exit: GameEffect()
}
