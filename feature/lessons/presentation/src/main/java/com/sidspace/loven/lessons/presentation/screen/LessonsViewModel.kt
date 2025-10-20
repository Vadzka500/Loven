package com.sidspace.loven.lessons.presentation.screen

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.ads.di.YandexRewardedAdManager
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.lessons.domain.usecase.GetLessonsUseCase
import com.sidspace.loven.lessons.domain.usecase.GetLivesCountUseCase
import com.sidspace.loven.lessons.presentation.mapper.toLessonUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonsViewModel @Inject constructor(
    private val getLessonsUseCase: GetLessonsUseCase, private val getLivesCountUseCase: GetLivesCountUseCase,
    private val rewardedAdManager: YandexRewardedAdManager
) : ViewModel() {

    private val _state = MutableStateFlow(LessonsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LessonsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        isCanStartGame()
    }

    fun showAds(activity: Activity){
        rewardedAdManager.show(activity)
    }

    fun onIntent(intent: LessonsIntent) {
        when (intent) {
            is LessonsIntent.SelectLesson -> {
                if (_state.value.isCanStartGame) onSelectLesson(intent.idLanguage, intent.idModule, intent.idLesson)
                else _state.update { it.copy(isShowNoLivesDialog = true) }
            }

            LessonsIntent.OnHideDialog -> {
                _state.update { it.copy(isShowNoLivesDialog = false) }
            }
        }
    }

    fun onSelectLesson(idLanguage: String, idModule: String, idLesson: String) {
        viewModelScope.launch {
            _effect.emit(LessonsEffect.ToGame(idLanguage, idModule, idLesson))
        }
    }

    fun getLessons(idLanguage: String, idModule: String) {
        viewModelScope.launch {
            when (val data = getLessonsUseCase(idLanguage, idModule)) {
                DomainResult.Error -> {
                    _state.update { it.copy(list = ResultUi.Error) }
                }

                is DomainResult.Success -> {
                    _state.update { it.copy(list = ResultUi.Success(data.data.map { it.toLessonUi() })) }
                }
            }
        }
    }

    fun isCanStartGame() {
        viewModelScope.launch {
            getLivesCountUseCase().collect {
                if (it > 0) {
                    _state.update { it.copy(isCanStartGame = true) }
                } else {
                    _state.update { it.copy(isCanStartGame = false) }
                }
            }
        }


    }
}
