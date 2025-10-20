package com.sidspace.loven.languages.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sidspace.loven.languages.presentation.screen.LanguageScreen

fun NavGraphBuilder.languageNavGraph(
    paddingValues: PaddingValues,
    onClick:(String) -> Unit
) {

    composable<Language>{
        LanguageScreen(modifier = Modifier.fillMaxSize().padding(paddingValues), onClick = onClick)
    }

}
