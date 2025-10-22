package com.sidspace.loven.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.sidspace.game.presentation.navigation.GameRoute
import com.sidspace.game.presentation.navigation.gameNavGraph
import com.sidspace.loven.authorization.presentation.navigation.Authorization
import com.sidspace.loven.authorization.presentation.navigation.authNavGraph
import com.sidspace.loven.home.presentation.navigation.Home
import com.sidspace.loven.home.presentation.navigation.homeNavGraph
import com.sidspace.loven.languages.presentation.navigation.Language
import com.sidspace.loven.languages.presentation.navigation.languageNavGraph
import com.sidspace.loven.lessons.presentation.navigation.LessonsRoute
import com.sidspace.loven.lessons.presentation.navigation.lessonsNavGraph
import com.sidspace.loven.modules.presentation.navigation.ModuleRoute
import com.sidspace.loven.modules.presentation.navigation.moduleNavGraph

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPaddingValues: PaddingValues
) {

    NavHost(
        navController = navController,
        startDestination = Authorization,
    ) {

        homeNavGraph(innerPaddingValues, toGameClick = {
            navController.navigate(Language)
        }, changeUser = {
            navController.navigate(Authorization)
        })

        languageNavGraph(innerPaddingValues, onClick = { idLanguage ->

            navController.navigate(ModuleRoute(idLanguage))
        })

        moduleNavGraph(innerPaddingValues, onClick = { idLanguage, idModule ->
            navController.navigate(LessonsRoute(idLanguage, idModule))
        })

        lessonsNavGraph(innerPaddingValues, onClick = { idLanguage, idModule, lessonId ->
            navController.navigate(GameRoute(idLanguage, idModule, lessonId))
        })

        gameNavGraph(innerPaddingValues, onBack = {
            navController.popBackStack()
        }, toModules = {
            navController.navigate(ModuleRoute(it)){
                popUpTo(navController.previousBackStackEntry?.destination?.route ?: return@navigate) {
                    inclusive = true
                }

                navController.popBackStack()

                launchSingleTop = true
            }
        })

        authNavGraph(toHomeScreen = {
            navController.navigate(Home) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        })

    }
}
