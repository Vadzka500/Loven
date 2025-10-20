package com.sidspace.loven.home.domain.usecase

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.home.domain.repository.HomeRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val homeRepository: HomeRepository) {
    operator fun invoke(): DomainResult<Unit> = homeRepository.signOut()
}
