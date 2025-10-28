@file:Suppress("TooManyFunctions")

package com.sidspace.game.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sidspace.game.presentation.model.WordsUi
import com.sidspace.game.presentation.uikit.DefaultCardColor
import com.sidspace.game.presentation.uikit.InCorrectBackgroundColor
import com.sidspace.game.presentation.uikit.PressCardColor
import com.sidspace.game.presentation.uikit.ProgressEndColor
import com.sidspace.game.presentation.uikit.ProgressStartColor
import com.sidspace.game.presentation.uikit.WhiteCardColor
import com.sidspace.loven.core.presentation.R
import com.sidspace.loven.core.presentation.model.GameModeUi
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.core.presentation.uikit.Sf_compact
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

private const val INCORRECT_ANIM_CYCLES_COUNT = 3
private const val INCORRECT_ANIM_AMPLITUDE = 10F
private const val INCORRECT_ANIM_CYCLE_DURATION = 250

private const val LONG_DURATION = 1500
private const val FAST_DURATION = 800

@Suppress("MagicNumber")
private val DELAYS_SHOW_WORDS = listOf(100, 200, 300, 400)

@Composable
fun GameScreen(
    idLanguage: String,
    idModule: String,
    idLesson: String,
    onBack: () -> Unit,
    toModules: (String) -> Unit,
    gameViewModel: GameViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val state = gameViewModel.state.collectAsState()


    LaunchedEffect(Unit) {
        gameViewModel.initParams(idLanguage, idModule, idLesson)

        gameViewModel.effect.collectLatest { effect ->
            when (effect) {
                is GameEffect.CorrectWords -> {

                }

                GameEffect.Exit -> {
                    println("exit")
                    onBack()
                }

                is GameEffect.InCorrectWords -> {

                }

                GameEffect.ToLessons -> {

                }

                GameEffect.ToModules -> {
                    toModules(idLanguage)
                }
            }
        }
    }

    BackHandler {
        gameViewModel.onIntent(GameIntent.ShowExitDialog)
    }

    GameContent(
        state = state, effect = gameViewModel.effect, onSelectWords = { wordRu, wordTranslate ->
            gameViewModel.onIntent(GameIntent.SelectWords(wordRu, wordTranslate))
        }, onBack = {
            gameViewModel.onIntent(GameIntent.ShowExitDialog)
        }, onHideExitDialog = {
            gameViewModel.onIntent(GameIntent.HideExitDialog)
        }, onExit = { gameViewModel.onIntent(GameIntent.Exit) },
        toModules = {
            gameViewModel.onIntent(GameIntent.ToModules)
        }, modifier = modifier
    )

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameContent(
    state: State<GameState>,
    onSelectWords: (String, String) -> Unit,
    onBack: () -> Unit,
    onExit: () -> Unit,
    toModules: () -> Unit,
    onHideExitDialog: () -> Unit,
    effect: SharedFlow<GameEffect>,
    modifier: Modifier = Modifier
) {


    when (val data = state.value.listWords) {
        ResultUi.Error -> Unit
        ResultUi.Loading -> Unit
        is ResultUi.Success -> {
            println("get new list")
            InitWords(
                words = data.data,
                timer = state.value.timer,
                onBack = onBack,
                selectedCount = state.value.countCurrentSelected,
                effect = effect,
                onSelectWords = onSelectWords,
                modifier = modifier
            )
        }
    }

    AnimatedContent(targetState = state.value.gameResult, transitionSpec = {
        fadeIn(
            animationSpec = tween(
                durationMillis = 500,
                delayMillis = 500
            )
        ) + scaleIn() with fadeOut(animationSpec = tween(durationMillis = 500))
    }) { result ->
        when (val data = result) {
            GameResult.EndLives -> {
                EndLivesScreen(toLessons = onExit)
            }

            GameResult.EndTime -> {
                EndTimeScreen(toLessons = onExit)
            }

            GameResult.None -> Unit
            is GameResult.SuccessGame -> {
                EndGameScreen(data.countStar, data.countError, toLessons = onExit)
            }

            is GameResult.SuccessLastGame -> {
                EndModuleScreen(toModules = toModules)
            }
        }
    }

    if (state.value.isShowExitDialog) {
        ShowExitDialog(onDismiss = onHideExitDialog, onExit = onExit)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowExitDialog(onDismiss: () -> Unit, onExit: () -> Unit, modifier: Modifier = Modifier) {


    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )


    ModalBottomSheet(
        sheetState = sheetState, onDismissRequest = {
            onDismiss()
        }, dragHandle = null
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            val textBody = "Вы действительно хотите выйти из урока?"

            Text(
                textBody,
                fontFamily = Sf_compact,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onExit()
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Выйти")
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
fun InitWords(
    words: WordsUi,
    timer: TimerState,
    selectedCount: Int,
    effect: SharedFlow<GameEffect>,
    onBack: () -> Unit,
    onSelectWords: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {

    var wrongPair by remember { mutableStateOf<Pair<String, String>?>(null) }
    var correctPair by remember { mutableStateOf<Pair<String, String>?>(null) }

    val selectWord = remember {
        mutableStateOf<String?>("")
    }

    val selectTranslateWord = remember {
        mutableStateOf<String?>("")
    }

    LaunchedEffect(Unit) {
        effect.collectLatest { effect ->
            when (effect) {
                is GameEffect.CorrectWords -> {
                    correctPair = effect.wordRu to effect.wordTranslate
                }

                is GameEffect.InCorrectWords -> {
                    wrongPair = effect.wordRu to effect.wordTranslate
                }

                GameEffect.ToLessons -> {
                    onBack()
                }

                else -> {

                }
            }
        }
    }

    Box(modifier = modifier) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            Row(modifier = Modifier.fillMaxWidth()) {


                LaunchedEffect(selectWord.value, selectTranslateWord.value) {
                    println("call event")
                    if (selectWord.value!!.isNotEmpty() && selectTranslateWord.value!!.isNotEmpty()) {
                        onSelectWords(selectWord.value!!, selectTranslateWord.value!!)
                    }
                }

                LaunchedEffect(words.listWordsRu) {
                    println("words count = " + words.listWordsRu.size)
                }

                WordColumnList(
                    words = words.listWordsRu,
                    selectedCount = selectedCount,
                    correctWord = correctPair?.first,
                    inCorrectWord = wrongPair?.first,
                    selectWord = selectWord,
                    clearInCorrect = {
                        wrongPair = wrongPair?.copy(first = "")
                    },
                    clearCorrect = {
                        correctPair = correctPair?.copy(first = "")
                    },
                    modifier = Modifier.weight(1f)
                )

                WordColumnList(
                    words = words.listWordsTranslate,
                    selectedCount = selectedCount,
                    correctWord = correctPair?.second,
                    inCorrectWord = wrongPair?.second,
                    selectWord = selectTranslateWord,
                    clearInCorrect = {
                        wrongPair = wrongPair?.copy(second = "")
                    },
                    clearCorrect = {
                        correctPair = correctPair?.copy(second = "")
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        BurningFuseTimerWithStarZones(timer = timer, words.type)

        ExitButton(onBack)
    }
}

@Composable
fun ExitButton(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 24.dp), contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    onBack()
                }
                .padding(8.dp),
            text = "Выйти",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}


@Composable
@Suppress("LongMethod")
fun WordColumnList(
    words: List<String?>,
    selectedCount: Int,
    correctWord: String?,
    inCorrectWord: String?,
    selectWord: MutableState<String?>,
    clearInCorrect: () -> Unit,
    clearCorrect: () -> Unit,
    modifier: Modifier = Modifier
) {


    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(words, key = { index, item -> item ?: "null_$index" }) { _, item ->

            var isVisible by remember {
                mutableStateOf(false)
            }

            if (correctWord == item) {
                isVisible = false
                selectWord.value = ""
                clearCorrect()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(94.dp)
            ) {
                if (item != null) {


                    val color = remember {
                        mutableStateOf(Color.Black)
                    }

                    val colorBorder = if (selectWord.value == item) PressCardColor
                    else DefaultCardColor


                    var isAminVisible by remember { mutableStateOf(false) }
                    var isError by remember { mutableStateOf(false) }

                    val t = remember { Animatable(0f) }
                    val totalCycles = INCORRECT_ANIM_CYCLES_COUNT
                    val amplitude = INCORRECT_ANIM_AMPLITUDE
                    val cycleDuration = INCORRECT_ANIM_CYCLE_DURATION

                    val rotation = remember(t.value) {
                        val maxT = totalCycles * 2 * PI
                        val damping = 1f - (t.value / maxT.toFloat()).coerceIn(0f, 1f)
                        amplitude * damping * sin(t.value)
                    }

                    if (inCorrectWord == item) {
                        isAminVisible = true
                        isError = true
                        selectWord.value = ""
                        clearInCorrect()
                    }

                    val backgroundColor by animateColorAsState(
                        targetValue = if (isError) InCorrectBackgroundColor
                        else if (selectWord.value == item) WhiteCardColor
                        else Color.White,
                        animationSpec = if (isError) {
                            tween(durationMillis = 200, easing = LinearEasing)
                        } else if (selectWord.value == item) {
                            tween(durationMillis = 0)
                        } else {
                            tween(durationMillis = 200, easing = LinearEasing)
                        }
                    )

                    LaunchedEffect(isAminVisible) {

                        if (isAminVisible) {


                            t.snapTo(0f)
                            t.animateTo(
                                targetValue = (totalCycles * 2 * PI).toFloat(),
                                animationSpec = tween(durationMillis = totalCycles * cycleDuration)
                            )

                            isAminVisible = false

                        }
                    }

                    LaunchedEffect(isError) {

                        if (isError) {


                            delay(INCORRECT_ANIM_CYCLE_DURATION.toLong())

                            isError = false

                        }
                    }

                    val duration = if (selectedCount == 1) LONG_DURATION else FAST_DURATION

                    AnimatedVisibility(
                        visible = isVisible,
                        exit = fadeOut(tween(durationMillis = duration, easing = FastOutSlowInEasing)),
                        enter = fadeIn(tween(durationMillis = 500, delayMillis = DELAYS_SHOW_WORDS.random()))
                    ) {
                        ElevatedPressableButton(
                            onClick = {
                                selectWord.value = if (selectWord.value != item) item
                                else ""
                            },
                            borderColor = colorBorder,
                            onPress = selectWord.value == item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .height(70.dp)
                                .graphicsLayer {
                                    rotationZ = rotation
                                },
                            backgroundColor = backgroundColor
                        ) {
                            Text(
                                item,
                                color = color.value,
                                fontSize = 16.sp,
                                fontFamily = Sf_compact,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    LaunchedEffect(Unit) {
                        isVisible = true
                    }
                }
            }
        }

    }
}


@Composable
@Suppress("MagicNumber")
fun BurningFuseTimerWithStarZones(
    timer: TimerState,
    gameMode: GameModeUi
) {

    val progress = timer.timeLeft / timer.timeTotal.toFloat()

    val flameAnim by rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        )
    )

    // Градиент фитиля
    val flameBrush = Brush.horizontalGradient(
        colors = listOf(
            ProgressStartColor.copy(alpha = 0.8f + 0.2f * flameAnim),
            ProgressEndColor.copy(alpha = 0.5f + 0.3f * flameAnim)
        )
    )

    val barWidth = remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 0.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(6.dp))
                .onGloballyPositioned { coords ->
                    barWidth.floatValue = coords.size.width.toFloat()
                }) {
            // Прогресс
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(flameBrush, shape = RoundedCornerShape(12.dp))
            )

            // Делители
            if (gameMode != GameModeUi.LAST_GAME) {
                timer.starThresholds.drop(1).forEach { threshold ->
                    val position = 1f - (threshold.toFloat() / timer.timeTotal)
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(2.dp)
                            .align(Alignment.CenterStart)
                            .offset {
                                IntOffset(
                                    x = (barWidth.value * position).toInt(), y = 0
                                )
                            }
                            .background(Color.White.copy(alpha = 0.9f)))
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (gameMode != GameModeUi.LAST_GAME) {
            TimerStart(timer, barWidth.value)
        }

    }
}

@Composable
fun TimerStart(timer: TimerState, barWidth: Float, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        timer.starThresholds.forEachIndexed { index, threshold ->
            val position = 1f - (threshold.toFloat() / timer.timeTotal)

            Row {
                repeat(index + 1) {
                    Image(
                        painter = painterResource(R.drawable.img_star),
                        contentDescription = null,
                        modifier = Modifier
                            .then(
                                if (index == 0) Modifier.padding(start = 3.dp)
                                else Modifier
                            )
                            .size(16.dp)
                            .offset {
                                IntOffset(
                                    x = (((barWidth) * position) - ((20.dp.value * (index + 1)))).toInt(),
                                    y = 0
                                )
                            })
                }
            }


        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Suppress("TooGenericExceptionCaught")
@Composable
fun ElevatedPressableButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color,
    onPress: Boolean,
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }
    var isTap by remember { mutableStateOf(false) }


    fun pressLogic() {
        isPressed = !isPressed
        println("press = " + isPressed)
        //if (!onPress) isPressed = false
    }

    if (!onPress && isPressed && isTap) {
        println("call")
        isTap = false
        pressLogic()
    }

    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 0.dp else 0.dp, animationSpec = tween(durationMillis = 0), label = "offsetY"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "CartoonButtonScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 8.dp, // при нажатии тень меньше
        label = "elevation"
    )

    var color by remember {
        mutableStateOf(Color.White)
    }

    Surface(
        shape = RoundedCornerShape(12.dp), color = color, tonalElevation = elevation, // для Material3
        shadowElevation = elevation, // для Material2
        modifier = modifier

            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = 3.dp, color = borderColor, shape = RoundedCornerShape(12.dp)
            )
            .offset(y = offsetY)
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    pressLogic()

                    //isPressed = true
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
                }, onTap = {
                    //isPressed = !isPressed

                    onClick()
                }

                )
            }) {
        Box(
            modifier = Modifier.background(backgroundColor), contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod", "MagicNumber")
@Composable
@Preview
fun EndGameScreen(starCount: Int = 2, inCorrectCount: Int = 1, toLessons: () -> Unit, modifier: Modifier = Modifier) {

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, confirmValueChange = { newValue ->
            newValue == SheetValue.Expanded
        })

    var isSheetOpen by remember { mutableStateOf(false) }


    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState, onDismissRequest = {

            }, dragHandle = null
        ) {

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Text("Поздравляем!", fontFamily = Sf_compact, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    repeat(starCount) {
                        Image(
                            painter = painterResource(R.drawable.img_star),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                var textBody: String

                if (inCorrectCount == 0) {
                    textBody = "Вы не допустили ни одной ошибки!"
                } else if (inCorrectCount == 1) {
                    textBody = "Вы допустили всего одну ошибку!"
                } else if (inCorrectCount == 2) {
                    textBody = "Вы допустили всего две ошибки!"
                } else if (inCorrectCount == 3) {
                    textBody = "Вы допустили всего три ошибки!"
                } else {
                    textBody = "Вы допустили много ошибок, но ничего страшного)"
                }

                Text(
                    textBody,
                    fontFamily = Sf_compact,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        isSheetOpen = false
                        toLessons()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Вернуться к урокам")
                }
            }
        }
    }

    // Основной экран
    LaunchedEffect(Unit) {
        isSheetOpen = true
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun EndModuleScreen(toModules: () -> Unit, modifier: Modifier = Modifier) {

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, confirmValueChange = { newValue ->
            newValue == SheetValue.Expanded
        })

    var isSheetOpen by remember { mutableStateOf(false) }


    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState, onDismissRequest = {

            }, dragHandle = null
        ) {

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    //.fillMaxHeight(0.5f) // можно ограничить высоту
                    .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Text("Поздравляем!", fontFamily = Sf_compact, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(com.sidspace.game.presentation.R.drawable.img_trophy),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                val textBody = "Вы прошли модуль"



                Text(
                    textBody,
                    fontFamily = Sf_compact,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        isSheetOpen = false
                        toModules()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Вернуться к модулям")
                }
            }
        }
    }

    // Основной экран
    LaunchedEffect(Unit) {
        isSheetOpen = true
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun EndTimeScreen(toLessons: () -> Unit, modifier: Modifier = Modifier) {

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, confirmValueChange = { newValue ->
            newValue == SheetValue.Expanded
        })

    var isSheetOpen by remember { mutableStateOf(false) }


    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState, onDismissRequest = {

            }, dragHandle = null
        ) {

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(com.sidspace.game.presentation.R.drawable.img_lose),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                val textBody = "У вас закончилось время, попробуйте еще раз"

                Text(
                    textBody,
                    fontFamily = Sf_compact,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        isSheetOpen = false
                        toLessons()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Вернуться к урокам")
                }
            }
        }
    }

    // Основной экран
    LaunchedEffect(Unit) {
        isSheetOpen = true
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun EndLivesScreen(toLessons: () -> Unit, modifier: Modifier = Modifier) {

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, confirmValueChange = { newValue ->
            newValue == SheetValue.Expanded
        })

    var isSheetOpen by remember { mutableStateOf(false) }


    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState, onDismissRequest = {

            }, dragHandle = null
        ) {

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(com.sidspace.game.presentation.R.drawable.img_lose),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                val textBody = "У вас закончились жизни("

                Text(
                    textBody,
                    fontFamily = Sf_compact,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        isSheetOpen = false
                        toLessons()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Вернуться к урокам")
                }
            }
        }
    }

    // Основной экран
    LaunchedEffect(Unit) {
        isSheetOpen = true
    }

}
