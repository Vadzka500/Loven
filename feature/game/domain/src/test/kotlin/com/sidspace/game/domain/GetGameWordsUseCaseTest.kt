package com.sidspace.game.domain

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.core.domain.model.GameModeDomain
import com.sidspace.game.Game
import com.sidspace.game.GameManager
import com.sidspace.game.domain.model.GameDomain
import com.sidspace.game.domain.model.Word
import com.sidspace.game.domain.repository.GameRepository

import com.sidspace.game.domain.usecase.GetGameWordsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class GetGameWordsUseCaseTest {

    @Test
    fun `test game object`() = runTest {
        val list = mutableListOf(
            mapOf("word_translate" to "house", "word_ru" to "дом"),
            mapOf("word_translate" to "home", "word_ru" to "дом"),
            mapOf("word_translate" to "room", "word_ru" to "комната"),
            mapOf("word_translate" to "door", "word_ru" to "дверь"),
            mapOf("word_translate" to "window", "word_ru" to "окно"),
            mapOf("word_translate" to "table", "word_ru" to "стол"),
            mapOf("word_translate" to "chair", "word_ru" to "стул"),
            mapOf("word_translate" to "bed", "word_ru" to "кровать"),
            mapOf("word_translate" to "kitchen", "word_ru" to "кухня"),
            mapOf("word_translate" to "bathroom", "word_ru" to "ванная"),
        )

        val listWords = list.map {
            Word(it["word_translate"]!!, it["word_ru"]!!)
        }.shuffled()

        val game = Game(listWords, "", "", "", GameModeDomain.DEFAULT)


        assertEquals(game.getInitialWords().listRuWords.size, 6)
        assertEquals(game.getInitialWords().listTranslateWords.size, 6)

    }
}
