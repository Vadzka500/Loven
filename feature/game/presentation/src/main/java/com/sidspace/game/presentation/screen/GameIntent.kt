package com.sidspace.game.presentation.screen

sealed interface GameIntent {
    data class SelectWords(val wordRu: String, val wordTranslate: String) : GameIntent
    data object StopGame : GameIntent
    data object ToLessons: GameIntent
}
