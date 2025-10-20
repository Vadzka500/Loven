package com.sidspace.loven.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sidspace.game.presentation.navigation.GameRoute
import com.sidspace.loven.authorization.presentation.navigation.Authorization
import com.sidspace.loven.core.presentation.R
import com.sidspace.loven.core.presentation.screen.HealthDialog
import com.sidspace.loven.home.presentation.navigation.Home
import com.sidspace.loven.languages.presentation.navigation.Language
import com.sidspace.loven.lessons.presentation.navigation.LessonsRoute
import com.sidspace.loven.modules.presentation.navigation.ModuleRoute


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LovenTopBar(
    navController: NavHostController,
    onShowAds: () -> Unit,
    onBackClick: (() -> Unit) = { navController.popBackStack() },
    userManager: com.sidspace.core.data.model.UserManager,
    modifier: Modifier = Modifier
) {

    val lifeState = userManager.lifeState.collectAsState()
    val timeState = userManager.timeUntilNextLife.collectAsState()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route?.substringBefore("/")

    val showTopBar = when (currentRoute) {
        null -> false
        Home::class.qualifiedName -> false
        Authorization::class.qualifiedName -> false
        //GameRoute::class.qualifiedName -> false
        else -> true
    }

    val isHideNavigationIcon = currentRoute == GameRoute::class.qualifiedName

    val title = when (currentRoute) {
        Language::class.qualifiedName -> "Выберите язык"
        ModuleRoute::class.qualifiedName -> "Выберите модуль"
        LessonsRoute::class.qualifiedName -> "Выберите урок"
        else -> ""
    }

    if (showTopBar) {
        TopAppBar(
            title = {
                TopBarContent(
                    lifeCount = lifeState.value,
                    timeNextLive = timeState.value,
                    onShowAds = onShowAds,
                    title = title, modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                )
            },
            navigationIcon = {

                IconButton(onClick = onBackClick) {
                    if (!isHideNavigationIcon)
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                }


            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                //containerColor = Color(0xFF6200EE), // фиолетовый, можно поменять
                titleContentColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = modifier
        )
    }


}

@Composable
fun TopBarContent(
    title: String,
    lifeCount: Long,
    timeNextLive: Long?,
    onShowAds: () -> Unit,
    modifier: Modifier = Modifier
) {

    val interactionSource = remember { MutableInteractionSource() }
    var showDialog by remember { mutableStateOf(false) }

    Row(modifier = modifier) {
        Text(text = title)
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.clickable(interactionSource = interactionSource, indication = null) {
            showDialog = true
        }) {
            Text(lifeCount.toString(), fontWeight = FontWeight.Medium, fontSize = 22.sp)
            Image(
                painter = painterResource(R.drawable.img_heart),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(28.dp)
            )
        }


    }

    if (showDialog) {
        HealthDialog(lifeCount, timeNextLive, onDismiss = {
            showDialog = false
        }, onShowAds = onShowAds)
    }

}
