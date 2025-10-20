package com.sidspace.loven.lessons.domain.usecase

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.lessons.domain.model.LessonDomain
import com.sidspace.loven.lessons.domain.repository.LessonsRepository
import javax.inject.Inject

class GetLessonsUseCase @Inject constructor(private val repository: LessonsRepository) {

    suspend operator fun invoke(idLanguage: String, idModule: String): DomainResult<List<LessonDomain>> =
        repository.getLessons(idLanguage, idModule)
}
