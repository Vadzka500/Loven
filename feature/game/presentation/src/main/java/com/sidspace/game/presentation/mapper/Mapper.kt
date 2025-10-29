package com.sidspace.game.presentation.mapper

import com.sidspace.game.domain.model.GameWords
import com.sidspace.game.presentation.model.WordsUi
import com.sidspace.loven.core.presentation.model.GameModeUi

fun GameWords.toWordUi(): WordsUi {
    return WordsUi(idLanguage, idModule, idLesson,listRuWords, listTranslateWords, GameModeUi.valueOf(type.toString()))
}
