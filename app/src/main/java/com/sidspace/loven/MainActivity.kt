package com.sidspace.loven

import android.os.Bundle
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
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.sidspace.loven.ui.theme.DefaultCardColor
import com.sidspace.loven.ui.theme.LovenTheme
import com.sidspace.loven.ui.theme.PressCardColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Firebase.firestore


        db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules").document("Mp9dlVP4MkXGPUoH6zb4")
            .collection("lesson").document("iZDDQQ2DkmL2M8pKBLBq").collection("words").get().addOnSuccessListener { result ->

                for(document in result){
                    println("data = " + document.data.values.take(1))
                }
            }.addOnFailureListener { exception ->
                println("error = $exception")
            }

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



                                    Column(modifier = Modifier.fillMaxWidth().height(105.dp)) {
                                        val color = remember {
                                            mutableStateOf(Color.Black)
                                        }

                                        val colorBorder = remember {
                                            mutableStateOf(DefaultCardColor)
                                        }

                                        var isVisible by remember {
                                            mutableStateOf(true)
                                        }

                                        AnimatedVisibility(visible = isVisible, exit = fadeOut(tween(durationMillis = 200))) {
                                            ElevatedPressableButton(
                                                onClick = {

                                                    isVisible = false
                                                    color.value = Color.Gray
                                                },
                                                borderColor = colorBorder,
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Text("Нажми", color = color.value)
                                            }
                                        }
                                    }
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

                                    val colorBorder = remember {
                                        mutableStateOf(DefaultCardColor)
                                    }

                                    ElevatedPressableButton(
                                        onClick = {
                                                  colorBorder.value = PressCardColor
                                        },
                                        borderColor = colorBorder,
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text("Нажми", color = color.value)
                                    }
                                }
                            }
                        }

                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ElevatedPressableButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: MutableState<Color>,
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
                color = borderColor.value,
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

