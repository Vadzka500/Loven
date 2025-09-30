package com.sidspace.game.domain.model

data class Game(
    val words: List<Word>
)


data class Word(
    val wordRu: String,
    val wordTranslate: String
)
