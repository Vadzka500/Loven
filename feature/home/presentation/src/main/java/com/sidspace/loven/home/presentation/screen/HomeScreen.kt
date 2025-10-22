package com.sidspace.loven.home.presentation.screen

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sidspace.loven.core.presentation.R
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.core.presentation.model.UserUi
import com.sidspace.loven.core.presentation.screen.HealthDialog
import com.sidspace.loven.core.presentation.uikit.Sf_compact
import com.sidspace.loven.utils.GameConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.sin
import kotlin.random.Random

val BorderColor = Color(0x5081D4FA)


@Composable

fun HomeScreen(
    modifier: Modifier = Modifier,
    toGameClick: () -> Unit,
    changeUser: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val state = homeViewModel.state.collectAsState()

    val context = LocalContext.current as Activity

    LaunchedEffect(Unit) {

        homeViewModel.getAccount()

        homeViewModel.effect.collectLatest { effect ->
            when (effect) {
                HomeEffect.ToChangeUser -> {
                    changeUser()
                }

                HomeEffect.ToGame -> toGameClick()
            }
        }
    }



    HomeContent(state, toGameClick = { homeViewModel.onIntent(HomeIntent.GameClick) }, changeUser = {
        homeViewModel.onIntent(
            HomeIntent.ChangeUser
        )
    }, onShowAds = {
        homeViewModel.showAds(context)
    }, modifier = modifier)

}

@Composable
fun HomeContent(
    state: State<HomeState>,
    toGameClick: () -> Unit,
    onShowAds: () -> Unit,
    changeUser: () -> Unit,
    modifier: Modifier = Modifier
) {

    val user = state.value.user
    val id = (user as? ResultUi.Success)?.data?.id ?: 0L

    Box(modifier = modifier) {

        when (val data = state.value.listWords) {
            is ResultUi.Success -> OptimizedFallingCardsBackground(
                listWords = data.data,
                modifier = Modifier.fillMaxSize()
            )

            else -> {

            }
        }


        Column {

            user.let {
                if (it is ResultUi.Success) {
                    UserContent(
                        user = it.data,
                        onShowAds = onShowAds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            /*AnimatedContent(
                targetState = id, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { _ ->

                user.let {
                    if (it is ResultUi.Success) {
                        UserContent(
                            user = it.data,
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                    }
                }

                *//*when (val data = user) {
                    is ResultUi.Success -> {
                        UserContent(
                            user = data.data,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }

                    else -> {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }*//*
            }*/

            AnimatedContent(targetState = id) { state ->

                user.let {
                    if (it is ResultUi.Success) {
                        GameButton(toGameClick)
                    }
                }
                /*when (state) {
                    is ResultUi.Success -> {
                        GameButton(toGameClick)
                    }

                    else -> {

                    }
                }*/
            }



            ChangeUserContent(
                changeUser,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

        }
    }
}

@Composable
fun ChangeUserContent(changeUser: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    changeUser()
                }
                .padding(8.dp),
            text = "Сменить аккаунт",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Sf_compact,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun GameButton(toGameClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {

        var isPressed by remember { mutableStateOf(false) }
        var isTap by remember { mutableStateOf(false) }

        val offsetY by animateDpAsState(
            targetValue = if (isPressed) 0.dp else 0.dp,
            animationSpec = tween(durationMillis = 0),
            label = "offsetY"
        )

        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.98f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "CartoonButtonScale"
        )

        val elevation by animateDpAsState(
            targetValue = if (isPressed) 1.dp else 10.dp,
            label = "elevation"
        )

        var color by remember {
            mutableStateOf(Color.White)
        }

        androidx.compose.material3.Surface(

            shape = RoundedCornerShape(12.dp),
            color = color,
            tonalElevation = elevation,
            shadowElevation = elevation,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 64.dp)

                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .border(
                    width = 5.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .offset(y = offsetY)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {

                            isPressed = true
                            try {
                                awaitRelease()
                                if (isTap) {
                                    isPressed = false
                                    isTap = false
                                } else {
                                    isTap = true
                                }
                            } catch (e: Exception) {
                                isPressed = false
                                e.printStackTrace()
                            }
                        },
                        onTap = {
                            isPressed = !isPressed


                            toGameClick()
                        }

                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 48.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {

                Text("К игре", fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = Sf_compact)
            }
        }

    }

}

@Composable
fun UserContent(user: UserUi, onShowAds: () -> Unit, modifier: Modifier = Modifier) {

    var showDialog by remember { mutableStateOf(false) }



    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Привет, ${user.name}", fontSize = 24.sp, fontWeight = FontWeight.Medium, fontFamily = Sf_compact)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    showDialog = true
                }
                .padding(8.dp)
        ) {
            Text(user.lifeCount.toString(), fontWeight = FontWeight.Medium, fontSize = 22.sp, fontFamily = Sf_compact)

            Image(
                painter = painterResource(R.drawable.img_heart),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(28.dp)
            )

            if (user.lifeCount < GameConstants.LIVES_MAX_COUNT) {
                Image(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(24.dp)
                )
            }
        }
    }

    if (showDialog) {
        HealthDialog(user.lifeCount, user.timeNextLife, onDismiss = { showDialog = false }, onShowAds = onShowAds)
    }
}


@Composable
fun OptimizedFallingCardsBackground(
    listWords: List<String>,
    modifier: Modifier = Modifier,
    cardCount: Int = 15
) {
    val cards = remember { List(listWords.size) { OptimizedCard(listWords[it]) } }
    var time by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            time += 0.016f // 60 FPS
            delay(16)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
        //.background(Color(0xFF0D47A1))
    ) {
        cards.forEach { card ->
            OptimizedCardItem(card = card, time = time)
        }
    }
}

@Composable
fun OptimizedCardItem(card: OptimizedCard, time: Float) {
    // Простая математика для плавной анимации
    val y = card.startY + (time * card.fallSpeed) % 1.2f
    val rotation = time * card.rotationSpeed * 360f
    val swingX = sin(time * card.swingSpeed) * card.swingAmount

    ElevatedCard(
        modifier = Modifier
            .offset(
                x = (card.startX + swingX).dp,
                y = (y * 1200).dp
            )
            .size(width = 100.dp, height = 50.dp)
            .graphicsLayer {
                rotationZ = rotation
                alpha = card.alpha
            }
            .border(
                width = 3.dp,
                color = Color(0x80D1C4E9),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(3.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = card.textCard,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }


    /*val elevation by animateDpAsState(
        targetValue = 5.dp, // при нажатии тень меньше
        label = "elevation"
    )

    var color by remember {
        mutableStateOf(Color.White)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color,
        tonalElevation = elevation, // для Material3
        shadowElevation = elevation, // для Material2
        modifier = Modifier
            .height(100.dp)
            .width(50.dp)
            .padding(8.dp)

            .border(
                width = 3.dp,
                color = Color(0x80D1C4E9),
                shape = RoundedCornerShape(12.dp)
            )
            .offset(
                x = (card.startX + swingX).dp,
                y = (y * 1200).dp
            )
            .graphicsLayer {
                rotationZ = rotation
                alpha = card.alpha
            }

    ) {
        Box(
            modifier = Modifier.background(Color.White),
            contentAlignment = Alignment.Center
        ) {

        }
    }*/
}

class OptimizedCard(val textCard: String) {
    val startX = Random.nextInt(-20, 380).toFloat()
    val startY = Random.nextFloat() * -0.5f
    val fallSpeed = Random.nextFloat() * 0.3f + 0.1f
    val rotationSpeed = Random.nextFloat() * 0.3f + 0.1f
    val swingSpeed = Random.nextFloat() * 2f + 1f
    val swingAmount = Random.nextFloat() * 10f
    val alpha = Random.nextFloat() * 0.3f + 0.4f
}
