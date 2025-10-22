package com.sidspace.game.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sidspace.game.presentation.screen.GameScreen
import com.sidspace.game.presentation.screen.GameViewModel

fun NavGraphBuilder.gameNavGraph(
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    toModules: (String) -> Unit
) {

    composable<GameRoute> {
        val game = it.toRoute<GameRoute>()
        GameScreen(
            idLanguage = game.idLanguage,
            idModule = game.idModule,
            idLesson = game.idLessons,
            onBack = onBack,
            toModules = toModules,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }

}
