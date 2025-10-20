package com.sidspace.loven.authorization.presentation.screen

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.authorization.domain.model.AuthDomainResult
import com.sidspace.loven.authorization.domain.usecase.CheckAuthorizationUseCase
import com.sidspace.loven.authorization.domain.usecase.SaveAccountUseCase
import com.sidspace.loven.authorization.presentation.model.AuthResultUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val checkAuthorizationUseCase: CheckAuthorizationUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthorizationState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AuthorizationEffect>(replay = 1)
    val effect: SharedFlow<AuthorizationEffect> = _effect.asSharedFlow()

    fun onIntent(intent: AuthorizationIntent) {
        when (intent) {
            AuthorizationIntent.ToHomeScreen -> {
                toHomeScreen()
            }
        }
    }

    init {
        checkAccount()
    }

    fun checkAccount() {

        viewModelScope.launch {
            when (checkAuthorizationUseCase()) {
                AuthDomainResult.Authorized -> {
                    _state.update { it.copy(user = AuthResultUi.Authorized) }
                }

                AuthDomainResult.Unauthorized -> {
                    _state.update { it.copy(user = AuthResultUi.Unauthorized) }
                }
            }
        }

    }

    fun saveAccount() {
        viewModelScope.launch {
            when (saveAccountUseCase()) {
                DomainResult.Error -> Unit
                is DomainResult.Success -> {
                    toHomeScreen()
                }
            }
        }

    }

    fun toHomeScreen() {
        viewModelScope.launch {

            _effect.emit(AuthorizationEffect.ToHomeScreen)

        }
    }

    fun signIn(signInLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveAccount()
            } else {
                Log.w("AuthViewModel", "signInWithCredential:failure", task.exception)
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }
}
