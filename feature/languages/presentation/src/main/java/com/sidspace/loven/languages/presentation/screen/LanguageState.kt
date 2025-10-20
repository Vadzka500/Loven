package com.sidspace.loven.languages.presentation.screen

import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.languages.presentation.model.LanguageUi

data class LanguageState(
    var listLanguages: ResultUi<List<LanguageUi>> = ResultUi.Loading
)
