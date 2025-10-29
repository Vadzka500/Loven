package com.sidspace.loven.authorization.presentation.model

sealed class AuthResultUi {
    object Authorized : AuthResultUi()
    object Unauthorized : AuthResultUi()
    object None : AuthResultUi()
}
