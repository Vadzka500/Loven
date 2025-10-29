package com.sidspace.loven.authorization.domain.usecase



import com.sidspace.loven.authorization.domain.model.AuthDomainResult
import com.sidspace.loven.authorization.domain.repository.AuthorizationRepository
import javax.inject.Inject

class CheckAuthorizationUseCase @Inject constructor(private val repository: AuthorizationRepository) {
    suspend operator fun invoke(): AuthDomainResult = repository.checkAccount()
}
