package com.sidspace.core.data.model

import com.google.firebase.firestore.PropertyName

data class UserModule(
    val starsCount: Long = 0,
    @get:PropertyName("isCompleted")
    val isCompleted: Boolean = false
)
