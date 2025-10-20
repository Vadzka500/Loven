package com.sidspace.loven.lessons.domain.repository

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.lessons.domain.model.LessonDomain
import kotlinx.coroutines.flow.Flow

interface LessonsRepository {

    suspend fun getLessons(idLanguage: String, idModule: String): DomainResult<List<LessonDomain>>

    fun getLivesCount(): Flow<Long>
}
