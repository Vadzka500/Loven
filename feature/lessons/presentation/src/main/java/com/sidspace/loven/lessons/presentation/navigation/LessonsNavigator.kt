package com.sidspace.loven.lessons.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sidspace.loven.lessons.presentation.screen.LessonsScreen

fun NavGraphBuilder.lessonsNavGraph(
    paddingValues: PaddingValues,
    onClick: (String, String, String) -> Unit
) {

    composable<LessonsRoute> { navBackStackEntry ->
        val lessons: LessonsRoute = navBackStackEntry.toRoute()
        LessonsScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            idLanguage = lessons.idLanguage,
            idModule = lessons.idModule,
            onSelectLesson = onClick
        )
    }
}

