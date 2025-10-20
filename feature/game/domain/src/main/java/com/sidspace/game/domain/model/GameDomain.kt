package com.sidspace.game.domain.model

import com.sidspace.core.domain.model.GameModeDomain

data class GameDomain(
    val words: List<Word>,
    val type: GameModeDomain
)

data class GameWords(
    val idLanguage: String,
    val idModule: String,
    val idLesson: String,
    var listRuWords: MutableList<String?>,
    var listTranslateWords: MutableList<String?>,
    val type: GameModeDomain
)

data class GameWordItem(
    val word: String
)

data class Word(
    val wordRu: String?,
    val wordTranslate: String?
)

sealed class GameStateDomain {
    object IsCorrect : GameStateDomain()
    object IsInCorrect : GameStateDomain()
    object EndLives : GameStateDomain()
}

sealed class GameLifeDomain {
    object ContinueGame : GameLifeDomain()
    object EndGame : GameLifeDomain()
}


