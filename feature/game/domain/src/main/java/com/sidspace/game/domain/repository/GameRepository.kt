package com.sidspace.game.domain.repository

import com.sidspace.core.domain.model.DomainResult

import com.sidspace.game.domain.model.GameDomain
import com.sidspace.game.domain.model.GameLifeDomain
import com.sidspace.game.domain.model.GameWordItem
import com.sidspace.game.domain.model.Word

interface GameRepository {

    suspend fun getGameWords(languageId: String, moduleId: String, lessonId: String): DomainResult<GameDomain>

    suspend fun saveLesson(languageId: String, moduleId: String, lessonId: String, starCount: Int): DomainResult<Unit>

    suspend fun inCorrectWords(): GameLifeDomain

}
