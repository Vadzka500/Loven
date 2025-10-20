package com.sidspace.core.data.model

import java.util.Date

data class UserSession(
    val id: String,
    val name: String?,
    val photoUrl: String?,
    val email: String?,
    var lifeCount: Long,
    val lastLifeTimestamp: Date?
)
