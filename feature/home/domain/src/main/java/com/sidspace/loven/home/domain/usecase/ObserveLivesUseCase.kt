package com.sidspace.loven.home.domain.usecase

import com.sidspace.loven.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLivesUseCase @Inject constructor(private val repository: HomeRepository) {
    operator fun invoke(): Flow<Long> = repository.observeLives()
}
