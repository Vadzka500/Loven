package com.sidspace.game.domain.usecase

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.game.GameManager
import com.sidspace.game.domain.model.GameWords
import com.sidspace.game.domain.repository.GameRepository
import javax.inject.Inject

class GetGameWordsUseCase @Inject constructor(
    private val repository: GameRepository, private val gameManager: GameManager
) {

    suspend operator fun invoke(
        languageId: String, moduleId: String, lessonId: String
    ): DomainResult<GameWords> {
        val data = repository.getGameWords(languageId, moduleId, lessonId)
        if (data is DomainResult.Success)
            return DomainResult.Success(
                gameManager.startGame(
                    languageId,
                    moduleId,
                    lessonId,
                    data.data.words.shuffled(),
                    data.data.type
                )
            )
        else
            return DomainResult.Error
    }

}
