package com.sidspace.game.domain.repository

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.game.domain.model.GameDomain
import com.sidspace.game.domain.model.GameLifeDomain

interface GameRepository {

    suspend fun getGameWords(languageId: String, moduleId: String, lessonId: String): DomainResult<GameDomain>

    suspend fun saveLesson(
        languageId: String,
        moduleId: String,
        lessonId: String,
        starCount: Int,
        isLastLesson: Boolean
    ): DomainResult<Unit>

    suspend fun inCorrectWords(): GameLifeDomain

}
