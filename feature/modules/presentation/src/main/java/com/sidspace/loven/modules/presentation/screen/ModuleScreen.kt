package com.sidspace.loven.modules.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sidspace.loven.core.presentation.R
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.core.presentation.uikit.Sf_compact
import com.sidspace.loven.modules.presentation.model.ModuleUi
import com.sidspace.loven.modules.presentation.uikit.ModuleProgressColor
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ModuleScreen(
    idLanguage: String,
    modifier: Modifier = Modifier,
    onClick: (String, String) -> Unit,
    moduleViewModel: ModuleViewModel = hiltViewModel()
) {
    val state = moduleViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        moduleViewModel.getModules(idLanguage)

        moduleViewModel.effect.collectLatest { effect ->
            when (effect) {
                is ModuleEffect.ToLessonsScreen -> onClick(effect.idLanguage, effect.idModule)
            }
        }
    }

    ModuleContent(modifier = modifier, state = state, onClick = { idLanguage, idModule ->
        moduleViewModel.onIntent(ModuleIntent.ToLessonsScreen(idLanguage, idModule))
    })

}


@Composable
fun ModuleContent(state: State<ModuleState>, onClick: (String, String) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        AnimatedContent(modifier = Modifier.fillMaxSize(), targetState = state.value.listModules, transitionSpec = {
            if (initialState is ResultUi.Success && targetState is ResultUi.Success) {
                fadeIn(animationSpec = tween(delayMillis = 0))
                    .togetherWith(fadeOut(animationSpec = tween(delayMillis = 0)))
            } else {
                fadeIn().togetherWith(fadeOut())
            }
        }) { result ->
            when (result) {
                ResultUi.Error -> Unit
                ResultUi.Loading -> Unit
                is ResultUi.Success -> ModuleList(list = result.data, onClick = onClick)
            }
        }
    }
}

@Composable
fun ModuleList(modifier: Modifier = Modifier, list: List<ModuleUi>, onClick: (String, String) -> Unit) {
    LazyColumn(
        modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(list, key = { it.id }) { item ->
            ModuleItem(item, onClick, modifier = Modifier.fillMaxWidth())
        }
    }


}

@Suppress("MagicNumber")
class ModuleUiProvider : PreviewParameterProvider<ModuleUi> {
    override val values = sequenceOf(
        ModuleUi("1", "Module 1", "Описание 1", "", "", 10, 12, 12, 12, true, 0),
        ModuleUi("1", "Module 1", "Описание 1", "", "", 10, 12, 12, 12, true, 0, false),
    )
}

@Composable
@Preview
fun ModuleItemPreview(@PreviewParameter(ModuleUiProvider::class) item: ModuleUi) {
    ModuleItem(item = item, onClick = { _, _ -> })
}

@Composable
@Preview
@Suppress("TooGenericExceptionCaught", "LongMethod")
fun ModuleItem(
    @PreviewParameter(ModuleUiProvider::class) item: ModuleUi,
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier) {

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
            targetValue = if (isPressed) 1.dp else 10.dp, // при нажатии тень меньше
            label = "elevation"
        )

        var color by remember {
            mutableStateOf(Color.White)
        }

        androidx.compose.material3.Surface(

            shape = RoundedCornerShape(12.dp),
            color = color,
            tonalElevation = elevation, // для Material3
            shadowElevation = elevation, // для Material2
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 16.dp)

                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .border(
                    width = 5.dp,
                    color = Color.White,
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

                            if (item.isEnableModule)
                                onClick(item.idLanguage, item.id)
                        }

                    )
                }
        ) {
            ModuleItemContent(item)
        }

    }


}

@Suppress("MagicNumber")
@Composable
fun ModuleItemContent(item: ModuleUi, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            alpha = 0.6f,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(150.dp)
                .width(200.dp)
                .offset(y = 30.dp, x = (-155).dp)
                .rotate(55f)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {


            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Sf_compact,
                    color = Color.Black
                )

                if (!item.isEnableModule)
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
            }



            Spacer(modifier = Modifier.height(8.dp))

            Text(
                item.description,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Sf_compact,
                color = Color.Black.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (!item.isEnableModule) {
                DisableModuleContent(item)

            } else {
                EnableModuleContent(item)
            }
        }

        if (item.isCompleted) {
            addCompletedImage()
        }

    }
}

@Composable
fun EnableModuleContent(item: ModuleUi, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BurningFuseTimerWithStarZones(
            item, modifier = Modifier
                .weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "${item.usersStars}/${item.maxStars}", fontFamily = Sf_compact,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.width(4.dp))

        Image(
            painter = painterResource(R.drawable.img_star),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun DisableModuleContent(item: ModuleUi, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {


        Text(
            "Еще ${item.starsToUnlock}",
            fontFamily = Sf_compact,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Black.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.width(4.dp))

        Image(
            painter = painterResource(R.drawable.img_star),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "для разблокировки",
            fontFamily = Sf_compact,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun addCompletedImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Image(
            painter = painterResource(com.sidspace.loven.modules.presentation.R.drawable.img_completed),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun BurningFuseTimerWithStarZones(
    item: ModuleUi,
    modifier: Modifier

) {
    val progress = item.usersStars.toFloat() / item.maxStars

    val flameBrush = Brush.horizontalGradient(
        colors = listOf(
            ModuleProgressColor.copy(alpha = 0.5f + 0.3f),
            ModuleProgressColor.copy(alpha = 0.5f + 0.3f),
        )
    )



    Box(
        modifier = modifier
            .height(12.dp)
            .background(Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(6.dp))) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(flameBrush, shape = RoundedCornerShape(12.dp))
        )
    }


}
