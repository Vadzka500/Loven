package com.sidspace.game.domain

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.core.domain.model.GameModeDomain
import com.sidspace.game.Game
import com.sidspace.game.GameManager
import com.sidspace.game.domain.model.GameWords
import com.sidspace.game.domain.usecase.GetUpdatedListUseCase
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class GetUpdatedListUseCaseTest {

    private val gameManager = mockk<GameManager>()
    private val useCase = GetUpdatedListUseCase(gameManager)


    @Test
    fun `success - get updated list`() = runTest {
        val gameMock = mockk<Game>(relaxed = true)
        val inputList = listOf("дом" to "house", "человек" to "people")
        val updated = GameWords("", "", "", mutableListOf("дом"), mutableListOf("house"), GameModeDomain.DEFAULT)

        coEvery { gameManager.getGame() } returns gameMock
        every { gameMock.getUpdatedList(inputList) } returns updated

        val result = useCase(inputList)

        assertTrue(result is DomainResult.Success)
        assertEquals(updated, (result as DomainResult.Success).data)

        verify(exactly = 1) {
            gameMock.getUpdatedList(inputList)
            gameManager.getGame()
        }
        confirmVerified(gameManager, gameMock)
    }

    @Test
    fun `error - get updated list`() = runTest {
        coEvery { gameManager.getGame() } returns null

        val result = useCase(emptyList())
        assertTrue(result is DomainResult.Error)
        verify { gameManager.getGame() }
        confirmVerified(gameManager)
    }

}
