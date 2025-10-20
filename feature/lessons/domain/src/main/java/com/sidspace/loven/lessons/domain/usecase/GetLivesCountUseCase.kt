package com.sidspace.loven.lessons.domain.usecase

import com.sidspace.loven.lessons.domain.repository.LessonsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLivesCountUseCase @Inject constructor(private var repository: LessonsRepository) {
    operator fun invoke(): Flow<Long> = repository.getLivesCount()
}
