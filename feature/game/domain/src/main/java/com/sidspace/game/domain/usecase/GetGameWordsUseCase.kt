package com.sidspace.game.domain.usecase

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.game.domain.model.Game
import com.sidspace.game.domain.repository.GameRepository
import javax.inject.Inject

class GetGameWordsUseCase @Inject constructor(private val repository: GameRepository) {

    suspend operator fun invoke(
        languageId: String,
        moduleId: String,
        lessonId: String
    ): DomainResult<Game> =
        repository.getGameWords(languageId, moduleId, lessonId)
}
