package com.sidspace.core.domain.model

data class UserDomain(
    val id: String,
    val name: String?,
    val photoUrl: String?,
    val email: String?,
    val lifeCount: Long
)
