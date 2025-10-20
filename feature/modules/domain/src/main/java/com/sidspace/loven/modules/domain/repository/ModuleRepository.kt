package com.sidspace.loven.modules.domain.repository

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.modules.domain.model.ModuleDomain

interface ModuleRepository {

    suspend fun getModulesByLanguage(id: String): DomainResult<List<ModuleDomain>>

}
