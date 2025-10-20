package com.sidspace.loven.core.presentation.model

data class UserUi(
    val id: String,
    val name: String?,
    val photo: String?,
    val email: String?,
    val lifeCount: Long,
    val timeNextLife: Long? = null
)
