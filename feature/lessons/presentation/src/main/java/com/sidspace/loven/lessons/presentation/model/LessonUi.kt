package com.sidspace.loven.lessons.presentation.model

import com.sidspace.loven.core.presentation.model.GameModeUi

data class LessonUi(
    val id: String,
    val idLanguage: String,
    val idModule: String,
    val type: GameModeUi,
    val starCount: Long?
)


