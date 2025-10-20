package com.sidspace.loven.authorization.presentation.screen

sealed interface AuthorizationIntent {

    object ToHomeScreen : AuthorizationIntent
}
