package com.sidspace.loven.modules.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.sidspace.loven.modules.presentation.model.ModuleUi

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
    }

    ModuleContent(modifier = modifier, state = state, onClick = onClick)

}

@Composable
fun ModuleContent(state: State<ModuleState>, onClick: (String, String) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        AnimatedContent(targetState = state.value.listModules) { result ->
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
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(list) { item ->
            ModuleItem(item, onClick, modifier = Modifier.fillMaxWidth())
        }
    }


}

@Composable
fun ModuleItem(item: ModuleUi, onClick: (String, String) -> Unit, modifier: Modifier = Modifier) {

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

                            onClick(item.idLanguage, item.id)
                        }

                    )
                }
        ) {
            Box(
                modifier = Modifier
                //.padding(horizontal = 48.dp, vertical = 24.dp)
                ,
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
                    /* .blur(
                         radiusX = 1.dp, radiusY = 1.dp, edgeTreatment = BlurredEdgeTreatment(
                             RoundedCornerShape(2.dp)

                         )
                     )*///.clip(CircleShape)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        item.name, fontSize = 24.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        item.description,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }


            }
        }

    }

}
