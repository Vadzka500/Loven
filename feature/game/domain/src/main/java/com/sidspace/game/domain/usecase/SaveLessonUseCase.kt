package com.sidspace.game.domain.usecase

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.game.domain.repository.GameRepository
import javax.inject.Inject

class SaveLessonUseCase @Inject constructor(private val repository: GameRepository) {
    suspend operator fun invoke(
        idLanguage: String,
        idModule: String,
        idLesson: String,
        isLastLesson: Boolean,
        countStars: Int
    ): DomainResult<Unit> = repository.saveLesson(idLanguage, idModule, idLesson, countStars, isLastLesson)
}
