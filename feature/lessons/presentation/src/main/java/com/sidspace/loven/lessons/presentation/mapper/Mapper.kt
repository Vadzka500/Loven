package com.sidspace.loven.lessons.presentation.mapper

import com.sidspace.loven.core.presentation.model.GameModeUi
import com.sidspace.loven.lessons.domain.model.LessonDomain

import com.sidspace.loven.lessons.presentation.model.LessonUi

fun LessonDomain.toLessonUi(): LessonUi {
    return LessonUi(id, idLanguage, idModule, GameModeUi.valueOf(type.toString()), countStar)
}
