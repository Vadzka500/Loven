package com.sidspace.game.domain

import com.sidspace.core.domain.model.GameModeDomain
import com.sidspace.game.Game
import com.sidspace.game.domain.model.Word
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals


class GetGameWordsUseCaseTest {

    @Test
    fun `test game object returns correct number of initial words`() {
        // Создаём фиксированный список слов
        val wordsList = listOf(
            Word("house", "дом"),
            Word("home", "дом"),
            Word("room", "комната"),
            Word("door", "дверь"),
            Word("window", "окно"),
            Word("table", "стол"),
            Word("chair", "стул"),
            Word("bed", "кровать"),
            Word("kitchen", "кухня"),
            Word("bathroom", "ванная")
        )

        // Фиксируем shuffle для стабильности
        val shuffledWords = wordsList.shuffled(java.util.Random(123))

        // Создаём объект Game
        val game = Game(shuffledWords, "", "", "", GameModeDomain.DEFAULT)

        // Получаем initialWords один раз
        val initialWords = game.getInitialWords()

        // Проверяем реальные значения, которые метод возвращает
        // По твоему сообщению ранее getInitialWords() возвращает 5 слов
            //throw RuntimeException("Этот тест должен упасть!")
        assertEquals(5, initialWords.listRuWords.size, "Должно быть 5 русских слов")
        assertEquals(5, initialWords.listTranslateWords.size, "Должно быть 5 слов для перевода")
    }
}
