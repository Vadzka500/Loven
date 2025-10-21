package com.sidspace.loven.lessons.presentation.screen

import android.app.Activity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sidspace.loven.core.presentation.model.GameModeUi
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.core.presentation.uikit.Sf_compact
import com.sidspace.loven.lessons.presentation.R
import com.sidspace.loven.lessons.presentation.model.LessonUi
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LessonsScreen(
    idLanguage: String,
    idModule: String,
    onSelectLesson: (String, String, String) -> Unit,
    lessonsViewModel: LessonsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state = lessonsViewModel.state.collectAsState()

    val activity = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        lessonsViewModel.getLessons(idLanguage, idModule)

        lessonsViewModel.effect.collectLatest { effect ->
            when (effect) {
                is LessonsEffect.ToGame -> {
                    onSelectLesson(effect.idLanguage, effect.idModule, effect.idLesson)
                }
            }
        }
    }

    LessonsContent(state = state, onSelectLesson = { idLanguage, idModule, idLesson ->
        lessonsViewModel.onIntent(LessonsIntent.SelectLesson(idLanguage, idModule, idLesson))
    }, onHideDialog = { lessonsViewModel.onIntent(LessonsIntent.OnHideDialog) }, showAds = {
        lessonsViewModel.showAds(activity)
    }, modifier = modifier)
}

@Composable
fun LessonsContent(
    state: State<LessonsState>,
    onSelectLesson: (String, String, String) -> Unit,
    onHideDialog: () -> Unit,
    showAds: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // AnimatedContent(targetState = state.value.list) { result ->
    when (val result = state.value.list) {
        ResultUi.Error -> Unit
        ResultUi.Loading -> Unit
        is ResultUi.Success -> {

            LessonsList(result.data, onSelectLesson = onSelectLesson, modifier = modifier)
        }
    }

    if (state.value.isShowNoLivesDialog) {
        NoLivesDialog(onClick = onHideDialog, showAds = showAds)
    }
    //  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoLivesDialog(onClick: () -> Unit, showAds:() -> Unit, modifier: Modifier = Modifier) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, confirmValueChange = { newValue ->
            newValue == SheetValue.Expanded
        })


    ModalBottomSheet(
        sheetState = sheetState, onDismissRequest = {

        }, dragHandle = null
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                //.fillMaxHeight(0.5f) // можно ограничить высоту
                .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(R.drawable.img_lose),
                contentDescription = null,
                modifier = Modifier.size(128.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            val textBody = "У вас закончились жизни, подождите восполнения или посмотрите рекламу"

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
                    onClick()
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Понятно",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onClick()
                    showAds()
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Посмотреть рекламу за 1 жизнь",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }


}

@Composable
fun LessonsList(list: List<LessonUi>, modifier: Modifier = Modifier, onSelectLesson: (String, String, String) -> Unit) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        itemsIndexed(list, key = { index, item -> item.id }) { index, title ->
            val mod = index % 8

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (mod) {
                    0 -> { // центр
                        Spacer(Modifier.weight(1f))
                        LessonItem(
                            title, index = index, isLast = list.size - 1 == index, onSelectLesson = onSelectLesson
                        )
                        Spacer(Modifier.weight(1f))
                    }

                    1 -> { // левее
                        Spacer(Modifier.weight(1f))
                        LessonItem(
                            title, index = index, isLast = list.size - 1 == index, onSelectLesson = onSelectLesson
                        )
                        Spacer(Modifier.weight(3f))

                    }

                    2 -> { // слева
                        LessonItem(
                            title, index = index, isLast = list.size - 1 == index, onSelectLesson = onSelectLesson
                        )
                        Spacer(Modifier.weight(2f))
                    }

                    3 -> { // левее
                        Spacer(Modifier.weight(1f))
                        LessonItem(
                            title, index = index, isLast = list.size - 1 == index, onSelectLesson = onSelectLesson
                        )
                        Spacer(Modifier.weight(3f))

                    }

                    4 -> { // центр
                        Spacer(Modifier.weight(1f))
                        LessonItem(
                            title, index = index, isLast = list.size - 1 == index, onSelectLesson = onSelectLesson
                        )
                        Spacer(Modifier.weight(1f))
                    }

                    5 -> { // правее
                        Spacer(Modifier.weight(3f))
                        LessonItem(
                            title, index = index, isLast = list.size - 1 == index, onSelectLesson = onSelectLesson
                        )
                        Spacer(Modifier.weight(1f))
                    }

                    6 -> { // справа
                        Spacer(Modifier.weight(2f))
                        LessonItem(
                            title, index = index, isLast = list.size - 1 == index, onSelectLesson = onSelectLesson
                        )
                    }

                    7 -> { // правее
                        Spacer(Modifier.weight(3f))
                        LessonItem(
                            title, index = index, isLast = list.size - 1 == index, onSelectLesson = onSelectLesson
                        )
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun LessonItem(
    item: LessonUi,
    index: Int,
    isLast: Boolean,
    onSelectLesson: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        var isPressed by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            isPressed = item.starCount == null
        }
        //isPressed = item.starCount == null

        var isTap by remember { mutableStateOf(false) }

        val offsetY by animateDpAsState(
            targetValue = if (isPressed) 0.dp else 0.dp, animationSpec = tween(durationMillis = 0), label = "offsetY"
        )

        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.98f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "CartoonButtonScale"
        )

        val elevation by animateDpAsState(
            targetValue = if (isPressed) 1.dp else 10.dp, // при нажатии тень меньше
            label = "elevation"
        )

        var color by remember {
            mutableStateOf(Color.White)
        }

        androidx.compose.material3.Surface(

            shape = CircleShape, color = color, tonalElevation = elevation, // для Material3
            shadowElevation = elevation, // для Material2
            modifier = Modifier
                .size(64.dp)
                //.padding(horizontal = 16.dp)

                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .border(
                    width = 5.dp, color = Color.White, shape = CircleShape
                )
                .offset(y = offsetY)
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {

                        isPressed = true
                        try {
                            awaitRelease()
                        } catch (e: Exception) {
                            if (item.starCount != null) isPressed = false
                            e.printStackTrace()
                        }
                    }, onTap = {
                        if (item.starCount != null) isPressed = !isPressed

                        if (item.starCount != null) onSelectLesson(item.idLanguage, item.idModule, item.id)

                    }

                    )
                }) {
            Box(
                modifier = Modifier
                //.padding(horizontal = 48.dp, vertical = 24.dp)
                , contentAlignment = Alignment.Center
            ) {


                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    /*Text(
                        "1", fontSize = 24.sp, fontWeight = FontWeight.Bold
                    )*/
                    val image = when (item.type) {
                        GameModeUi.LAST_GAME -> R.drawable.img_trophy_1
                        GameModeUi.DEFAULT -> R.drawable.img_medal
                        GameModeUi.SWAP -> R.drawable.img_swap
                    }

                    Image(
                        painter = painterResource(image),
                        contentDescription = item.id,
                        colorFilter = if (item.starCount == null) ColorFilter.tint(Color.Gray.copy(alpha = 0.5f))
                        else if (image == R.drawable.img_swap) ColorFilter.tint(Color.Green)
                        else null,
                        modifier = Modifier.then(
                            if (item.type == GameModeUi.SWAP) {
                                Modifier.size(24.dp)
                            } else Modifier.size(32.dp)
                        )
                    )

                }


            }
        }
        StarsContent(
            starCount = item.starCount ?: 0, modifier = Modifier
                .padding(top = 6.dp)
                .height(18.dp)
        )

    }
}

@Composable
fun StarsContent(starCount: Long, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(starCount.toInt()) {
            Image(
                painter = painterResource(R.drawable.img_star),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
