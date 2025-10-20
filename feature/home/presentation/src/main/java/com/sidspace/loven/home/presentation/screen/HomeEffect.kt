package com.sidspace.loven.home.presentation.screen

sealed class HomeEffect {
    data object ToChangeUser : HomeEffect()
    data object ToGame: HomeEffect()
}
