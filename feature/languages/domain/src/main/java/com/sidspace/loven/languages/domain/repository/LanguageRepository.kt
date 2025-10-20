package com.sidspace.loven.languages.domain.repository

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.languages.domain.model.LanguageDomain

interface LanguageRepository {

    suspend fun getLanguages(): DomainResult<List<LanguageDomain>>
}
