package com.sidspace.loven.authorization.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sidspace.loven.authorization.presentation.screen.AuthorizationScreen

fun NavGraphBuilder.authNavGraph(
    toHomeScreen: () -> Unit
) {

    composable<Authorization> {
        AuthorizationScreen(toHomeScreen = toHomeScreen, modifier = Modifier.fillMaxSize())
    }

}
