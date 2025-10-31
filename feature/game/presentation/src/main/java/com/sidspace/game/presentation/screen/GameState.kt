package com.sidspace.game.presentation.screen

import com.sidspace.game.presentation.model.WordsUi
import com.sidspace.loven.core.presentation.model.GameModeUi
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.utils.GameConstants

data class GameState(
    val listWords: ResultUi<WordsUi> = ResultUi.Loading,
    val countCurrentSelected: Int = 0,
    val gameMode: GameModeUi = GameModeUi.DEFAULT,

    val countCorrectWords: Int = 0,
    val countInCorrectWords: Int = 0,

    val gameResult: GameResult = GameResult.None,
    val timer: TimerState = TimerState(),

    val isShowExitDialog: Boolean = false
)

data class TimerState(
    val timeTotal: Int = GameConstants.TOTAL_TIME,
    val timeLeft: Int = GameConstants.TOTAL_TIME,
    val isRunning: Boolean = false,
    val starThresholds: List<Int> = listOf(
        GameConstants.ONE_STAR_THRESHOLD, GameConstants.TWO_STAR_THRESHOLD,
        GameConstants.THREE_STAR_THRESHOLD
    )
)

enum class GameMode {
    DEFAULT, SWAP, LAST_GAME
}

sealed class GameResult {
    object EndLives : GameResult()
    object EndTime : GameResult()
    data class SuccessGame(val countStar: Int, val countError: Int) : GameResult()
    data class SuccessLastGame(val countError: Int) : GameResult()
    object None : GameResult()
}


