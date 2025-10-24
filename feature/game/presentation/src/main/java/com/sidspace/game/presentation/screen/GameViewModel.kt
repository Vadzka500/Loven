package com.sidspace.game.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.game.domain.model.GameStateDomain
import com.sidspace.game.domain.usecase.GetGameWordsUseCase
import com.sidspace.game.domain.usecase.GetUpdatedListUseCase
import com.sidspace.game.domain.usecase.IsCorrectWordUseCase
import com.sidspace.game.domain.usecase.SaveLessonUseCase
import com.sidspace.game.presentation.mapper.toWordUi
import com.sidspace.loven.core.presentation.model.GameModeUi
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.utils.GameConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getGameWordsUseCase: GetGameWordsUseCase,
    private val isCorrectWordUseCase: IsCorrectWordUseCase,
    private val getUpdatedListUseCase: GetUpdatedListUseCase,
    private val saveLessonUseCase: SaveLessonUseCase
) : ViewModel() {

    lateinit var idLanguage: String
    lateinit var idModule: String
    lateinit var idLesson: String

    private companion object {
        const val TIMER_INTERVAL = 1000L
        const val DELAY_INTERVAL = 800L
        const val COUNT_STEPS_DELAY = 3

    }

    fun initParams(idLanguage: String, idModule: String, idLesson: String) {
        this.idLanguage = idLanguage
        this.idModule = idModule
        this.idLesson = idLesson
        getInitialWord()
    }


    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<GameEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.SelectWords -> {
                isCorrectWords(intent.wordRu, intent.wordTranslate)
            }


            GameIntent.StopGame -> TODO()
            GameIntent.ToLessons -> {
                toLessons()
            }

            GameIntent.Exit -> {
                _state.update { it.copy(isShowExitDialog = false) }
                exitFromGame()
            }

            GameIntent.ShowExitDialog -> {
                _state.update { it.copy(isShowExitDialog = true) }
            }

            GameIntent.HideExitDialog -> {
                _state.update { it.copy(isShowExitDialog = false) }
            }

            GameIntent.ToModules -> {
                toModules()
            }
        }
    }

    private fun toModules() {
        viewModelScope.launch {
            _effect.emit(GameEffect.ToModules)
        }
    }

    private fun exitFromGame() {
        viewModelScope.launch {
            _effect.emit(GameEffect.Exit)
        }
    }

    private fun toLessons() {
        viewModelScope.launch {
            _effect.emit(GameEffect.ToLessons)
        }
    }


    private fun getStars(): Int {
        return when {
            _state.value.timer.timeTotal - _state.value.timer.timeLeft < _state.value.timer.starThresholds[2] ->
                GameConstants.THREE_STARS

            _state.value.timer.timeTotal - _state.value.timer.timeLeft < _state.value.timer.starThresholds[1] ->
                GameConstants.TWO_STARS

            _state.value.timer.timeTotal - _state.value.timer.timeLeft < _state.value.timer.starThresholds[0] ->
                GameConstants.ONE_STARS

            else -> 0
        }
    }

    private var timerJob: Job? = null

    private fun startTimer() {
        timerJob?.cancel()
        _state.update { it.copy(timer = it.timer.copy(isRunning = true)) }
        timerJob = viewModelScope.launch {
            while (_state.value.timer.timeLeft > 0
                && _state.value.timer.isRunning
                && _state.value.gameResult == GameResult.None
            ) {
                delay(TIMER_INTERVAL)
                _state.update { it.copy(timer = it.timer.copy(timeLeft = it.timer.timeLeft - 1)) }
            }
            if (_state.value.timer.timeLeft <= 0) {
                _state.update { it.copy(timer = it.timer.copy(isRunning = false)) }
                _state.update { it.copy(gameResult = GameResult.EndTime) }
            }
        }
    }

    private var job: Job? = null

    private fun startJob() {
        job?.cancel() // отменяем старый, если он ещё выполняется
        job = CoroutineScope(Dispatchers.IO).launch {
            val listJob = list.toList()
            list.clear()
            println("start job = $listJob")
            when (val data = getUpdatedListUseCase(listJob)) {
                DomainResult.Error -> Unit
                is DomainResult.Success -> {
                    println("update1 = " + data.data)
                    val isEnd = data.data.let {
                        it.listRuWords.all { it == null } && it.listTranslateWords.all { it == null }
                    }

                    println("is end = " + isEnd)

                    if (isEnd) {
                        val starsCount = getStars()
                        val isLastLesson = state.value.gameMode == GameModeUi.LAST_GAME
                        when (saveLessonUseCase(
                            idLanguage,
                            idModule,
                            idLesson,
                            isLastLesson,
                            starsCount

                        )) {
                            DomainResult.Error -> Unit
                            is DomainResult.Success -> {
                                _state.update {
                                    if (!isLastLesson) {
                                        it.copy(
                                            gameResult = GameResult.SuccessGame(
                                                starsCount,
                                                _state.value.countInCorrectWords
                                            )
                                        )
                                    } else {
                                        it.copy(
                                            gameResult = GameResult.SuccessLastGame(
                                                _state.value.countInCorrectWords
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    _state.update { it.copy(listWords = ResultUi.Success(data.data.toWordUi())) }
                }

                null -> Unit
            }


        }
    }


    private var isFast = false

    private var list = mutableListOf<Pair<String, String>>()

    fun isCorrectWords(wordRu: String, wordTranslate: String) {
        viewModelScope.launch {

            when (isCorrectWordUseCase(wordRu, wordTranslate)) {
                GameStateDomain.EndLives -> {

                    _state.update { it.copy(countInCorrectWords = _state.value.countInCorrectWords + 1) }
                    showInCorrectWords(wordRu, wordTranslate)

                    _state.update {
                        it.copy(
                            gameResult = GameResult.EndLives
                        )
                    }
                }

                GameStateDomain.IsCorrect -> {
                    _state.update { it.copy(countCorrectWords = _state.value.countCorrectWords + 1) }
                    _state.update { it.copy(countCurrentSelected = _state.value.countCurrentSelected + 1) }
                    showCorrectWords(wordRu, wordTranslate)
                    list.add(wordRu to wordTranslate)

                    val count = _state.value.countCurrentSelected // читаем уже обновлённое
                    isFast = if (count == 1) false else true
                    var countSteps = COUNT_STEPS_DELAY

                    viewModelScope.launch {

                        if (!isFast) {
                            while (!isFast && countSteps > 0) {
                                delay(DELAY_INTERVAL)
                                countSteps--
                            }
                        } else {
                            delay(DELAY_INTERVAL)
                        }


                        _state.update { it.copy(countCurrentSelected = _state.value.countCurrentSelected - 1) }


                        if (job?.isActive != true && list.isNotEmpty()) {
                            println("start job 1")
                            startJob()
                        } else {
                            println("start job 2 = " + list)
                        }

                    }
                }

                GameStateDomain.IsInCorrect -> {
                    _state.update { it.copy(countInCorrectWords = _state.value.countInCorrectWords + 1) }
                    showInCorrectWords(wordRu, wordTranslate)
                }
            }
        }
    }

    fun showInCorrectWords(wordRu: String, wordTranslate: String) {
        viewModelScope.launch {
            _effect.emit(GameEffect.InCorrectWords(wordRu, wordTranslate))
        }
    }

    fun showCorrectWords(wordRu: String, wordTranslate: String) {
        viewModelScope.launch {
            _effect.emit(GameEffect.CorrectWords(wordRu, wordTranslate))
        }
    }


    fun getInitialWord() {
        viewModelScope.launch {
            when (val data = getGameWordsUseCase(idLanguage, idModule, idLesson)) {
                DomainResult.Error -> Unit
                is DomainResult.Success -> {
                    _state.update {
                        it.copy(
                            listWords = ResultUi.Success(data.data.toWordUi()),
                            gameMode = GameModeUi.valueOf(data.data.type.toString())
                        )
                    }
                    startTimer()
                }
            }
        }
    }
}
