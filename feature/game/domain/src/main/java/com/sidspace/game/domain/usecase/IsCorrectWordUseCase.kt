package com.sidspace.game.domain.usecase

import com.sidspace.game.GameManager
import com.sidspace.game.domain.model.GameLifeDomain
import com.sidspace.game.domain.model.GameStateDomain
import com.sidspace.game.domain.repository.GameRepository
import javax.inject.Inject

class IsCorrectWordUseCase @Inject constructor(
    private val repository: GameRepository,
    private val gameManager: GameManager
) {
    suspend operator fun invoke(wordRu: String, wordTranslate: String): GameStateDomain {

        val isCorrect = gameManager.getGame()?.checkWord(wordRu, wordTranslate) ?: false
        if (!isCorrect) {
            return when (repository.inCorrectWords()) {
                GameLifeDomain.ContinueGame -> GameStateDomain.IsInCorrect
                GameLifeDomain.EndGame -> GameStateDomain.EndLives
            }
        }
        return GameStateDomain.IsCorrect
    }
}
