package com.sidspace.loven.authorization.presentation.model

import com.sidspace.loven.authorization.domain.model.AuthDomainResult

sealed class AuthResultUi {
    object Authorized : AuthResultUi()
    object Unauthorized : AuthResultUi()
    object None : AuthResultUi()
}
