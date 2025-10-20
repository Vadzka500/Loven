package com.sidspace.loven.authorization.domain.repository

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.authorization.domain.model.AuthDomainResult


interface AuthorizationRepository {
    suspend fun checkAccount(): AuthDomainResult
    suspend fun saveAccount(): DomainResult<Unit>
}
