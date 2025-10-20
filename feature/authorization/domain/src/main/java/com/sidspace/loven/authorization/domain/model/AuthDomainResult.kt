package com.sidspace.loven.authorization.domain.model

sealed class AuthDomainResult {
    object Authorized : AuthDomainResult()
    object Unauthorized : AuthDomainResult()
}
