package com.sidspace.game.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data class GameRoute(val idLanguage: String, val idModule: String, val idLessons: String)
