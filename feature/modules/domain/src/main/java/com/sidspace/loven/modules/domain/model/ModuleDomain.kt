package com.sidspace.loven.modules.domain.model

data class ModuleDomain(
    val id: String,
    val idLanguage: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val lessonsCount: Long,
    val starsToEnable: Long,
    val usersStars: Long,
    val maxStars: Long,
    val isEnableModule: Boolean,
    val starsToUnlock: Long
)
