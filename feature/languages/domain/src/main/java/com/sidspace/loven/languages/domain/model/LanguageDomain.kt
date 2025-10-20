package com.sidspace.loven.languages.domain.model


data class LanguageDomain(
    val id: String,
    val nameLanguage: String,
    val imageUrl: String,
    val position: Long,
    val isEnable: Boolean
)
