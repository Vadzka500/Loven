package com.sidspace.loven.core.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sidspace.loven.core.presentation.R
import com.sidspace.loven.core.presentation.uikit.Sf_compact
import com.sidspace.loven.utils.GameConstants


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun HealthDialog(
    livesCount: Long,
    time: Long?,
    onDismiss: () -> Unit,
    onShowAds: () -> Unit,
) {


    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    var isSheetOpen by remember { mutableStateOf(false) }


    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState, onDismissRequest = {
                onDismiss()
            }, dragHandle = null
        ) {
            HealthDialogSheetContent(livesCount, time, onDismiss, onShowAds)

        }
    }

    LaunchedEffect(Unit) {
        isSheetOpen = true
    }

}

@Composable
fun HealthDialogSheetContent(
    livesCount: Long,
    time: Long?,
    onDismiss: () -> Unit,
    onShowAds: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                livesCount.toString(),
                fontFamily = Sf_compact,
                fontWeight = FontWeight.Medium,
                fontSize = 96.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(16.dp))

            Image(
                painter = painterResource(R.drawable.img_heart),
                contentDescription = null,
                modifier = Modifier.size(128.dp)
            )
        }


        Spacer(modifier = Modifier.height(24.dp))


        if (livesCount < GameConstants.LIVES_MAX_COUNT) {
            AddTextTimeNextLiveToDialog(time)
        } else {
            Text(
                "У вас максимальное количество жизней",
                fontFamily = Sf_compact,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = {
                onDismiss()
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

        if (livesCount < GameConstants.LIVES_MAX_COUNT) {
            AddAdsButtonToDialog(onDismiss, onShowAds)
        }
    }
}

@Composable
fun AddTextTimeNextLiveToDialog(time: Long?, modifier: Modifier = Modifier) {
    time?.let { it ->
        val minutes = (it / 60000) % 60
        val seconds = (it / 1000) % 60

        Text(
            "Следующая жизнь через ${minutes.let { if (it < 10) "0$it" else it }}:${seconds.let { if (it < 10) "0$it" else it }}" +
                    "\nили посмотрите рекламу",
            fontFamily = Sf_compact,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

    }
}

@Composable
fun AddAdsButtonToDialog(onDismiss: () -> Unit, onShowAds: () -> Unit, modifier: Modifier = Modifier) {
    Spacer(modifier = Modifier.height(8.dp))
    Button(
        onClick = {
            onDismiss()
            onShowAds()
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

