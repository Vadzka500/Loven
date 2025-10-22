package com.sidspace.loven.modules.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sidspace.loven.modules.presentation.screen.ModuleEffect
import com.sidspace.loven.modules.presentation.screen.ModuleScreen

fun NavGraphBuilder.moduleNavGraph(
    paddingValues: PaddingValues,
    onClick: (String, String) -> Unit
) {

    composable<ModuleRoute> { navBackStackEntry ->

        val module: ModuleRoute = navBackStackEntry.toRoute()

        ModuleScreen(
            idLanguage = module.idLanguage,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            onClick = onClick
        )
    }
}
