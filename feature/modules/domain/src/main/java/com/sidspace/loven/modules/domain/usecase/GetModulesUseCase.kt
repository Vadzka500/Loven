package com.sidspace.loven.modules.domain.usecase

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.modules.domain.model.ModuleDomain
import com.sidspace.loven.modules.domain.repository.ModuleRepository
import javax.inject.Inject

class GetModulesUseCase @Inject constructor(private val repository: ModuleRepository) {

    suspend operator fun invoke(idLanguage: String): DomainResult<List<ModuleDomain>> =
        repository.getModulesByLanguage(idLanguage)
}
