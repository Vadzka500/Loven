package com.sidspace.loven.lessons.presentation.screen

import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.lessons.presentation.model.LessonUi

data class LessonsState(
    val list: ResultUi<List<LessonUi>> = ResultUi.Loading,
    val isCanStartGame: Boolean = false,
    val isShowNoLivesDialog: Boolean = false
)
