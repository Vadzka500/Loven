package com.sidspace.loven.home.domain.repository

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.core.domain.model.UserDomain
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun getBackgroundWords(): DomainResult<List<String>>

    suspend fun getAccount(): DomainResult<UserDomain>

    fun signOut(): DomainResult<Unit>

    fun observeLives(): Flow<Long>

    fun observeTimeToNextLive(): Flow<Long?>
}
