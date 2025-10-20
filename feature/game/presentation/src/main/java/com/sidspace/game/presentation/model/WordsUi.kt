package com.sidspace.game.presentation.model

import com.sidspace.loven.core.presentation.model.GameModeUi

data class WordsUi(
    val idLanguage: String,
    val idModule: String,
    val idLesson: String,
    val listWordsRu: List<String?>,
    val listWordsTranslate: List<String?>,
    val type: GameModeUi
)
