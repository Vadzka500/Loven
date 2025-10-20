package com.sidspace.loven.languages.domain.usecase

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.languages.domain.model.LanguageDomain
import com.sidspace.loven.languages.domain.repository.LanguageRepository
import javax.inject.Inject

class GetLanguagesUseCase @Inject constructor(private val repository: LanguageRepository) {
    suspend operator fun invoke(): DomainResult<List<LanguageDomain>> = repository.getLanguages()
}
