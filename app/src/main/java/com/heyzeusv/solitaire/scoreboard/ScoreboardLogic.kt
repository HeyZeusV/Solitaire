package com.heyzeusv.solitaire.scoreboard

import androidx.compose.runtime.snapshots.Snapshot
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.MoveResult.*
import com.heyzeusv.solitaire.util.StartingScore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 *  Data manager for Scoreboard
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
class ScoreboardLogic(private val viewModelScope: CoroutineScope) {
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
    fun reset(startingScore: StartingScore) {
        _moves.value = 0
        resetTimer()
        _score.value = startingScore.amount
        _historyList.clear()
        recordHistory()
    }

    /**
     *  Called after user taps on any pile. Updates moves/score depending on [result].
     */
    fun handleMoveResult(result: MoveResult) {
        when (result) {
            Move -> _moves.value++
            MoveScore -> {
                _moves.value++
                _score.value++
            }
            MoveMinusScore -> {
                _moves.value++
                _score.value--
            }
            FullPileScore -> _score.value += 13
            Illegal -> return
        }
        appendHistory()
    }

    /**
     *  Returns [LastGameStats] with given [gameWon] and most updated stat values.
     */
    fun retrieveLastGameStats(gameWon: Boolean): LastGameStats {
        return LastGameStats(
            gameWon,
            _moves.value,
            _time.value,
            _score.value
        )
    }

    /**
     *  Cancel Timer coroutine
     */
    fun onCleared() { timerJob?.cancel() }
}