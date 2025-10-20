package com.sidspace.loven.authorization.presentation.screen


import com.sidspace.loven.authorization.presentation.model.AuthResultUi

data class AuthorizationState(
    val user: AuthResultUi = AuthResultUi.None
)
