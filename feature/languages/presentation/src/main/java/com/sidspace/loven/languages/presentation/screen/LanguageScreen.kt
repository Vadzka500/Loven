package com.sidspace.loven.languages.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.core.presentation.uikit.Sf_compact
import com.sidspace.loven.languages.presentation.model.LanguageUi
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LanguageScreen(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    languageViewModel: LanguageViewModel = hiltViewModel()
) {

    val state = languageViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        languageViewModel.effect.collectLatest { effect ->
            when (effect) {
                is LanguageEffect.ToModulesScreen -> {
                    onClick(effect.idLanguage)
                }
            }
        }
    }

    LanguagesContent(modifier = modifier, state = state, onClick = {
        languageViewModel.onIntent(LanguageIntent.SelectLanguage(it))
    })

}

@Composable
fun LanguagesContent(modifier: Modifier = Modifier, state: State<LanguageState>, onClick: (String) -> Unit) {
    Box(modifier = modifier.padding(top = 8.dp)) {
        AnimatedContent(targetState = state.value.listLanguages) { result ->
            when (result) {
                ResultUi.Error -> Unit
                ResultUi.Loading -> Unit
                is ResultUi.Success -> {
                    ListLanguages(list = result.data, onClick = onClick)
                }
            }
        }
    }
}

@Composable
fun ListLanguages(modifier: Modifier = Modifier, list: List<LanguageUi>, onClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(list) { item ->
            LanguageCard(modifier = Modifier.fillMaxWidth(), item = item, onClick = onClick)
        }
    }
}

@Suppress("TooGenericExceptionCaught", "MagicNumber")
@Composable
fun LanguageCard(modifier: Modifier = Modifier, item: LanguageUi, onClick: (String) -> Unit) {

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
                            } finally {
                                //color = Color.Red

                            }
                        },
                        onTap = {
                            isPressed = !isPressed

                            if (item.isEnable) {
                                onClick(item.id)
                            }
                        }

                    )
                }
        ) {
            Box(
                modifier = Modifier,
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
                        .offset(y = 30.dp, x = -155.dp)
                        .rotate(55f)

                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        item.nameLanguage,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Sf_compact,
                        color = Color.Black
                    )

                    if (!item.isEnable) {
                        Text(
                            text = "Скоро",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Sf_compact,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (item.isEnable)
                                Modifier.background(Color.White.copy(alpha = 0f))
                            else
                                Modifier.background(Color.White.copy(alpha = 0.5f))
                        )
                ) { }

            }
        }

    }

}
