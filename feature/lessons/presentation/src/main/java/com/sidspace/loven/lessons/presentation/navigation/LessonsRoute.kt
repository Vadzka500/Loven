package com.sidspace.loven.lessons.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data class LessonsRoute(
    val idLanguage: String,
    val idModule: String
)
