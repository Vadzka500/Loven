package com.sidspace.loven.home.presentation.screen

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.ads.di.YandexRewardedAdManager
import com.sidspace.loven.core.presentation.mapper.toUserUi
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.home.domain.usecase.GetAccountUseCase
import com.sidspace.loven.home.domain.usecase.GetBackgroundWordsUseCase
import com.sidspace.loven.home.domain.usecase.ObserveLivesUseCase
import com.sidspace.loven.home.domain.usecase.ObserveTimeNextLiveUseCase
import com.sidspace.loven.home.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getBackgroundWordsUseCase: GetBackgroundWordsUseCase,
    private val adsManager: YandexRewardedAdManager,
    private val observeLivesUseCase: ObserveLivesUseCase,
    private val observeTimeNextLiveUseCase: ObserveTimeNextLiveUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    init {
        getBackgroundWords()
    }

    fun loadAds(activity: Activity){
        adsManager.load(activity)
    }

    fun showAds(activity: Activity) {
        adsManager.show(activity)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.ChangeUser -> signOut()
            HomeIntent.GameClick -> toGame()
        }
    }

    fun toGame() {
        viewModelScope.launch {
            _effect.emit(HomeEffect.ToGame)
        }
    }

    fun signOut() {
        when (val data = signOutUseCase()) {
            DomainResult.Error -> Unit
            is DomainResult.Success -> {
                _state.update { it.copy(user = ResultUi.Loading) }
                toChangeUser()
            }
        }
    }

    fun toChangeUser() {
        viewModelScope.launch {
            _effect.emit(HomeEffect.ToChangeUser)
        }
    }

    fun getAccount() {
        viewModelScope.launch {
            when (val data = getAccountUseCase()) {
                DomainResult.Error -> {
                    _state.update { it.copy(user = ResultUi.Error) }
                }

                is DomainResult.Success -> {
                    _state.update { it.copy(user = ResultUi.Success(data.data.toUserUi())) }
                    observeLives()
                    observeTimeNextLive()
                }
            }
        }

    }

    fun observeLives() {
        viewModelScope.launch {
            observeLivesUseCase().collect { lives ->
                _state.update { currentState ->
                    val updatedUser = when (val userResult = currentState.user) {
                        is ResultUi.Success -> {
                            userResult.data.copy(lifeCount = lives)
                                .let { ResultUi.Success(it) }
                        }
                        else -> userResult
                    }

                    currentState.copy(user = updatedUser)
                }
            }
        }
    }

    fun observeTimeNextLive() {
        viewModelScope.launch {
            observeTimeNextLiveUseCase().collect { lives ->
                _state.update { currentState ->
                    val updatedUser = when (val userResult = currentState.user) {
                        is ResultUi.Success -> {
                            userResult.data.copy(timeNextLife = lives)
                                .let { ResultUi.Success(it) }
                        }
                        else -> userResult
                    }

                    currentState.copy(user = updatedUser)
                }
            }
        }
    }

    fun getBackgroundWords() {
        when (val data = getBackgroundWordsUseCase()) {
            DomainResult.Error -> {
                _state.update { it.copy(listWords = ResultUi.Error) }
            }

            is DomainResult.Success -> {
                _state.update { it.copy(listWords = ResultUi.Success(data.data)) }
            }
        }
    }

}
