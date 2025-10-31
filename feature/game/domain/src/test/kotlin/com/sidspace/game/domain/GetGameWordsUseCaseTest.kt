package com.sidspace.game.domain

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.core.domain.model.GameModeDomain
import com.sidspace.game.GameManager
import com.sidspace.game.domain.model.GameDomain
import com.sidspace.game.domain.model.GameWords
import com.sidspace.game.domain.model.Word
import com.sidspace.game.domain.repository.GameRepository
import com.sidspace.game.domain.usecase.GetGameWordsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals


class GetGameWordsUseCaseTest {

    private val repository = mockk<GameRepository>()
    private val gameManager = mockk<GameManager>()
    private val useCase = GetGameWordsUseCase(repository, gameManager)

    @Test
    fun `test game success repository`() = runTest {


        val list = listOf(
            Word("house", "дом"),
            Word("room", "комната"),
            Word("door", "дверь"),
            Word("window", "окно"),
            Word("table", "стол"),
            Word("chair", "стул"),
            Word("bed", "кровать"),
            Word("kitchen", "кухня"),
            Word("bathroom", "ванная")
        )

        val gameDomainInput = GameDomain(
            words = list,
            type = GameModeDomain.DEFAULT
        )

        val gameDomainExpected = GameWords(
            "",
            "",
            "",
            list.map { it.wordRu }.take(5).toMutableList(),
            list.map { it.wordTranslate }.take(5).toMutableList(),
            GameModeDomain.DEFAULT
        )



        coEvery { repository.getGameWords("", "", "") } returns DomainResult.Success(gameDomainInput)

        coEvery { gameManager.startGame("", "", "", any(), GameModeDomain.DEFAULT) } returns gameDomainExpected

        val result = useCase("", "", "")

        assertTrue(result is DomainResult.Success)

        val gameWords = (result as DomainResult.Success).data
        assertEquals(5, gameWords.listRuWords.size)
        assertEquals(5, gameWords.listTranslateWords.size)

        coVerify(exactly = 1) {
            repository.getGameWords("", "", "")
            gameManager.startGame("", "", "", any(), GameModeDomain.DEFAULT)
        }
        confirmVerified(repository, gameManager)
    }

    @Test
    fun `test game object error repository`() = runTest {

        coEvery { repository.getGameWords("", "", "") } returns DomainResult.Error

        val result = useCase("", "", "")

        assertTrue(result is DomainResult.Error)


        coVerify(exactly = 1) {
            repository.getGameWords("", "", "")
        }
        confirmVerified(repository)
    }
}
