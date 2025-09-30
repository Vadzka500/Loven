package com.sidspace.game.domain.repository

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.game.domain.model.Game

interface GameRepository {

    suspend fun getGameWords(languageId: String, moduleId: String, lessonId: String): DomainResult<Game>

}
