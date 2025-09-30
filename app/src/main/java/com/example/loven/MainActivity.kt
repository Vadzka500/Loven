package com.example.loven

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loven.ui.theme.LovenTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LovenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Row {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                repeat(5) {

                                    CartoonButton2(
                                        text = "Играть!",
                                        onClick = { /* Действие */ },
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    /*Column(modifier = Modifier.fillMaxWidth().height(105.dp)) {
                                        val color = remember {
                                            mutableStateOf(Color.Black)
                                        }
                                        var isVisible by remember {
                                            mutableStateOf(true)
                                        }

                                        AnimatedVisibility(visible = isVisible, exit = fadeOut()) {
                                            ElevatedPressableButton(
                                                onClick = {

                                                    isVisible = false
                                                    color.value = Color.Gray
                                                },
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Text("Нажми", color = color.value)
                                            }
                                        }
                                    }*/
                                }

                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                repeat(5) {
                                    val color = remember {
                                        mutableStateOf(Color.Black)
                                    }
                                    ElevatedPressableButton(
                                        onClick = { color.value = Color.Red },
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text("Нажми", color = color.value)
                                    }
                                }
                            }
                        }


                        /* Canvas(modifier = Modifier.size(100.dp)) {
                             drawCircle(Color.Red, radius = 50f, center = Offset(100f, 100f))
                         }*/
                    }

                }
            }
        }
    }
}


@Composable
fun CartoonButtonV1_5(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "CartoonScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = 8f
                shape = RoundedCornerShape(20.dp)
                clip = true
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF176), Color(0xFFFFC107)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 5.dp,
                color = Color(0xFF5D4037),
                shape = RoundedCornerShape(20.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            }
            .padding(horizontal = 28.dp, vertical = 16.dp)
    ) {
        // Shine Layer (fake reflection)
        Canvas(
            modifier = Modifier
                .matchParentSize()
        ) {
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                ),
                cornerRadius = CornerRadius(20.dp.toPx()),
                size = Size(size.width, size.height / 2)
            )
        }

        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3E2723),
            style = TextStyle(
                fontFamily = FontFamily.Cursive,
                shadow = Shadow(
                    color = Color.White,
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
    }
}


@Composable
fun CartoonButtonV2(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "BounceScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(50))
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFEE58), Color(0xFFFDD835))
                )
            )
            .border(
                width = 6.dp,
                color = Color(0xFF6D4C41),
                shape = RoundedCornerShape(50)
            )
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(50),
                ambientColor = Color(0xFFFFF176),
                spotColor = Color(0xFFFFF176)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            }
            .padding(horizontal = 40.dp, vertical = 20.dp)
    ) {
        Text(
            text = text,
            fontSize = 26.sp,
            color = Color(0xFF3E2723),
            fontWeight = FontWeight.ExtraBold,
            style = TextStyle(
                fontFamily = FontFamily.Cursive,
                shadow = Shadow(
                    color = Color.White,
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
    }
}

@Composable
fun SoftCartoonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFFFF8E1),  // мягкий жёлтый
    borderColor: Color = Color(0xFFB39DDB),      // нежный фиолетовый
    contentColor: Color = Color(0xFF4A148C)      // тёмный фиолетовый
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "SoftCartoonScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = 8f
                shape = RoundedCornerShape(32)
                clip = true
            }
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.85f)
                    )
                ),
                shape = RoundedCornerShape(32)
            )
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(32)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            }
            .padding(horizontal = 28.dp, vertical = 16.dp)
    ) {
        // Лёгкий верхний блик
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                ),
                cornerRadius = CornerRadius(32f)
            )
        }

        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = contentColor,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.White,
                    offset = Offset(1f, 1f),
                    blurRadius = 2f
                )
            )
        )
    }
}




@Composable
fun CartoonButton2(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "CartoonButtonScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = 12f
                shape = RoundedCornerShape(24.dp)
                clip = true
            }
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFE082), Color(0xFFFFA000))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 6.dp,
                color = Color(0xFF5D4037), // коричневая обводка
                shape = RoundedCornerShape(24.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            }
            .padding(horizontal = 32.dp, vertical = 18.dp)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4E342E), // тёмный текст
            style = TextStyle(
                fontFamily = FontFamily.Cursive,
                shadow = Shadow(
                    color = Color.White,
                    offset = Offset(2f, 2f),
                    blurRadius = 3f
                )
            )
        )
    }
}

@Composable
fun CartoonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "CartoonButtonScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(
                color = Color.Yellow,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 4.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    onClick()
                },
                onClickLabel = "Cartoon Button"
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            style = TextStyle(
                fontFamily = FontFamily.Cursive
            )
        )
    }
}



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ElevatedPressableButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = Color(0x80D1C4E9),
    content: @Composable () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }
    var isTap by remember { mutableStateOf(false) }

    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 0.dp else 0.dp,
        animationSpec = tween(durationMillis = 0),
        label = "offsetY"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
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

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color,
        tonalElevation = elevation, // для Material3
        shadowElevation = elevation, // для Material2
        modifier = modifier

            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = 3.dp,
                color = borderColor,
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
                        //isPressed = !isPressed

                        onClick()
                    }

                )
            }
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 48.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

