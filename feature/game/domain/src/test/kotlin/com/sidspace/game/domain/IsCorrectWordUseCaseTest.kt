package com.sidspace.game.domain

import com.sidspace.game.Game
import com.sidspace.game.GameManager
import com.sidspace.game.domain.model.GameStateDomain
import com.sidspace.game.domain.repository.GameRepository
import com.sidspace.game.domain.usecase.IsCorrectWordUseCase
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class IsCorrectWordUseCaseTest {

    private val repository = mockk<GameRepository>()
    private val gameManager = mockk<GameManager>()

    private val useCase = IsCorrectWordUseCase(repository, gameManager)

    @Test
    fun `is correct words`() = runTest {
        val wordRu = "Футболка"
        val wordTranslate = "t-shirt"

        val gameMock = mockk<Game>(relaxed = true)
        coEvery { gameManager.getGame() } returns gameMock
        every { gameMock.checkWord(wordRu, wordTranslate) } returns true

        val result = useCase(wordRu, wordTranslate)

        assertTrue(result is GameStateDomain.IsCorrect)

        verify(exactly = 1) {
            gameMock.checkWord(wordRu, wordTranslate)
            gameManager.getGame()
        }

        confirmVerified(gameManager, gameMock)
    }
}
