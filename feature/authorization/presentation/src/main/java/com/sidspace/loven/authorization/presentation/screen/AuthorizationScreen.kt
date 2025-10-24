package com.sidspace.loven.authorization.presentation.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.sidspace.loven.authorization.presentation.R
import com.sidspace.loven.authorization.presentation.model.AuthResultUi
import com.sidspace.loven.core.presentation.uikit.Sf_compact
import kotlinx.coroutines.flow.collectLatest


@Composable
fun AuthorizationScreen(
    toHomeScreen: () -> Unit,
    authorizationViewModel: AuthorizationViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val state = authorizationViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        authorizationViewModel.effect.collectLatest { effect ->
            when (effect) {
                AuthorizationEffect.ToHomeScreen -> toHomeScreen()
            }
        }
    }

    when (state.value.user) {
        AuthResultUi.Authorized -> {
            authorizationViewModel.onIntent(AuthorizationIntent.ToHomeScreen)
        }

        AuthResultUi.None -> {

        }
        AuthResultUi.Unauthorized -> {
            AuthorizationContent(modifier = modifier)
        }
    }

}

@Composable

fun AuthorizationContent(
    authorizationViewModel: AuthorizationViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)


                if (account.idToken != null) {
                    authorizationViewModel.firebaseAuthWithGoogle(account.idToken!!)
                } else {
                    Log.w("AuthCheckScreen", "Google sign in failed: account or idToken is null")
                }
            } catch (e: ApiException) {
                Log.w("AuthCheckScreen", "Google sign in failed", e)
            }
        } else {
            Log.w("AuthCheckScreen", "Google sign in cancelled or failed")
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Image(painter = painterResource(R.drawable.image), contentDescription = null, contentScale = ContentScale.Crop)

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            AuthorizationTitle()

            Spacer(modifier = Modifier.height(24.dp))
            AuthorizationBody()
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.background),
                onClick = {
                    authorizationViewModel.signIn(signInLauncher)
                }) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_google),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Начать c Google",
                        fontFamily = Sf_compact,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun AuthorizationBody() {
    Text(
        text = stringResource(R.string.authorization_body),
        fontFamily = Sf_compact,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.background,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun AuthorizationTitle(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.authorization_title),
        fontFamily = Sf_compact,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = MaterialTheme.colorScheme.background,
        lineHeight = 35.sp,
        textAlign = TextAlign.Center
    )
}
