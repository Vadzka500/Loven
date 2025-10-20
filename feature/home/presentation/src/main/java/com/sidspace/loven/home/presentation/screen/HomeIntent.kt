package com.sidspace.loven.home.presentation.screen

sealed interface HomeIntent {

    object ChangeUser : HomeIntent

    object GameClick: HomeIntent

}
