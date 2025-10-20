package com.sidspace.loven.home.presentation.screen

import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.core.presentation.model.UserUi

data class HomeState(
    val listWords: ResultUi<List<String>> = ResultUi.Loading,
    val user: ResultUi<UserUi> = ResultUi.Loading

)
