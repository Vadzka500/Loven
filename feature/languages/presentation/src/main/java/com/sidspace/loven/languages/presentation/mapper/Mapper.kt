package com.sidspace.loven.languages.presentation.mapper

import com.sidspace.loven.languages.domain.model.LanguageDomain
import com.sidspace.loven.languages.presentation.model.LanguageUi

fun List<LanguageDomain>.toLanguageUi(): List<LanguageUi> {
    return this.map { it.toLanguageUi() }
}

fun LanguageDomain.toLanguageUi(): LanguageUi {
    return LanguageUi(this.id, this.nameLanguage, this.imageUrl, this.isEnable, this.position)
}
