package com.sidspace.loven.home.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sidspace.loven.home.presentation.screen.HomeScreen

fun NavGraphBuilder.homeNavGraph(
    paddingValues: PaddingValues, toGameClick: () -> Unit, changeUser: () -> Unit
) {

    composable<Home>() {
        HomeScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), toGameClick = toGameClick, changeUser = changeUser
        )
    }

}
