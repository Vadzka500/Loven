package com.sidspace.loven.modules.presentation.screen

import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.modules.presentation.model.ModuleUi

data class ModuleState(
    var listModules: ResultUi<List<ModuleUi>> = ResultUi.Loading
)
