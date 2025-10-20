package com.sidspace.loven.authorization.domain.usecase

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.authorization.domain.repository.AuthorizationRepository
import javax.inject.Inject

class SaveAccountUseCase @Inject constructor(private val repository: AuthorizationRepository) {
    suspend operator fun invoke(): DomainResult<Unit> = repository.saveAccount()
}
