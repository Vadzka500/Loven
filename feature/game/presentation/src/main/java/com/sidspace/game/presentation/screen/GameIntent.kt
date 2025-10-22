package com.sidspace.game.presentation.screen

sealed interface GameIntent {
    data class SelectWords(val wordRu: String, val wordTranslate: String) : GameIntent
    data object StopGame : GameIntent
    data object ToLessons: GameIntent
    object ShowExitDialog: GameIntent
    object HideExitDialog: GameIntent
    object Exit: GameIntent
    object ToModules: GameIntent
}
