package com.sidspace.loven.lessons.domain.model

import com.sidspace.core.domain.model.GameModeDomain

data class LessonDomain(
    val id: String,
    val idLanguage: String,
    val idModule: String,
    val type: GameModeDomain,
    val countStar: Long?
)


