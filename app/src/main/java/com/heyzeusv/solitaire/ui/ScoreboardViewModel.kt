package com.heyzeusv.solitaire.ui

import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.data.ScoreHistory
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.MoveResult.MOVE
import com.heyzeusv.solitaire.util.MoveResult.MOVE_SCORE
import com.heyzeusv.solitaire.util.MoveResult.ILLEGAL
import com.heyzeusv.solitaire.util.MoveResult.MOVE_MINUS_SCORE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  Data manager for Scoreboard
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class ScoreboardViewModel @Inject constructor() : ViewModel() {

    // increases whenever a card/s moves
    private val _moves = MutableStateFlow(0)
    val moves: StateFlow<Int> get() = _moves

    // tracks how long user has played current game for
    private val _time = MutableStateFlow(0L)
    val time: StateFlow<Long> get() = _time
    private var timerJob: Job? = null

    // one point per card in Foundation, 52 is max
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> get() = _score

    private val _historyList = mutableListOf<ScoreHistory>()
    val historyList: List<ScoreHistory> get() = _historyList
    private lateinit var currentStep: ScoreHistory

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _time.value++
            }
        }
    }

    fun pauseTimer() = timerJob?.cancel()

    private fun resetTimer() {
        timerJob?.cancel()
        _time.value = 0
    }

    fun jobIsCancelled(): Boolean = timerJob?.isCancelled ?: true

    /**
     *  Takes a [Snapshot] of current Score value and stores them in a [ScoreHistory] object. We
     *  then immediately dispose of the [Snapshot] to avoid memory leaks.
     */
    private fun recordHistory() {
        val currentSnapshot = Snapshot.takeMutableSnapshot()
        currentSnapshot.enter {
            currentStep = ScoreHistory(_score.value)
        }
        currentSnapshot.dispose()
    }

    /**
     *  Adds [currentStep] to our [_historyList] list before overwriting [currentStep] using
     *  [recordHistory]. This should be call after every legal move.
     */
    private fun appendHistory() {
        // limit number of undo steps to 15
        if (_historyList.size == 15) _historyList.removeFirst()
        _historyList.add(currentStep)
        recordHistory()
    }

    /**
     *  We pop the last [ScoreHistory] value in [_historyList] and use it to restore the game
     *  state to it.
     */
    fun undo() {
        if (_historyList.isNotEmpty()) {
            val step = _historyList.removeLast()
            _score.value = step.score
            // called to ensure currentStep stays updated.
            recordHistory()
            // counts as a move still
            _moves.value++
        }
    }

    /**
     *  Resets stats for next game.
     */
    fun reset() {
        _moves.value = 0
        resetTimer()
        _score.value = 0
        recordHistory()
    }

    /**
     *  Called after user taps on any pile. Updates moves/score depending on [result].
     */
    fun handleMoveResult(result: MoveResult) {
        when (result) {
            MOVE -> { _moves.value++ }
            MOVE_SCORE -> {
                _moves.value++
                _score.value++
            }
            MOVE_MINUS_SCORE -> {
                _moves.value++
                _score.value--
            }
            ILLEGAL -> { return }
        }
        appendHistory()
    }

    /**
     *  Returns [LastGameStats] with given [gameWon] and most updated stat values.
     *  [autoCompleteCorrection] determines how many moves and score should be added due to auto
     *  complete.
     */
    fun retrieveLastGameStats(
        gameWon: Boolean,
        autoCompleteCorrection: Int = 0
    ): LastGameStats {
        return LastGameStats(
            gameWon,
            _moves.value + autoCompleteCorrection,
            _time.value,
            _score.value + autoCompleteCorrection
        )
    }

    override fun onCleared() {
        super.onCleared()
        // cancel coroutine
        timerJob?.cancel()
    }

    init {
        reset()
    }
}