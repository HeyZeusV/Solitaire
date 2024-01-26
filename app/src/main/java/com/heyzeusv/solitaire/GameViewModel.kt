package com.heyzeusv.solitaire

import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 *  Data manager for Board.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
class GameViewModel : ViewModel() {

    // increases whenever a card/s moves
    private val _moves = MutableStateFlow(0)
    val moves: StateFlow<Int> get() = _moves

    // tracks how long user has played current game for
    private val _timer = MutableStateFlow(0L)
    val timer: StateFlow<Long> get() = _timer
    private var timerJob: Job? = null

    // one point per card in Foundation, 52 is max
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> get() = _score

    private val _deck = Deck()
    val deck: Deck get() = _deck

    private val _waste = Waste()
    val waste: Waste get() = _waste

    private val _foundation = Suits.entries.map { Foundation(it) }.toMutableList()
    val foundation: List<Foundation> get() = _foundation

    private val _tableau = MutableList(7) { Tableau() }
    val tableau: List<Tableau> get() = _tableau

    private val historyList = mutableListOf<History>()
    private lateinit var currentStep: History

    private val _gameWon = MutableStateFlow(false)
    val gameWon: StateFlow<Boolean> get() = _gameWon

    // TODO: Check if timer needs start/pause in onResume()/onPause()
    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _timer.value++
            }
        }
    }

    fun pauseTimer() = timerJob?.cancel()

    private fun resetTimer() {
        timerJob?.cancel()
        _timer.value = 0
    }

    // goes through all card piles in the game and resets them for a new game
    fun reset() {
        // reset stats
        resetTimer()
        _moves.value = 0
        _score.value = 0
        // shuffled 52 card deck
        _deck.reset()
        // empty foundations
        _foundation.forEach { it.resetCards() }
        // each pile in the tableau has 1 more card than the previous
        _tableau.forEachIndexed { i, tableau ->
            val cards = MutableList(i + 1) { _deck.drawCard() }
            tableau.reset(cards)
        }
        // clear the waste pile
        _waste.resetCards()
        historyList.clear()
        recordHistory()
        _gameWon.value = false
    }

    // runs when user taps on deck
    fun onDeckTap() {
        // add card to waste if deck is not empty and flip it face up
        if (_deck.gameDeck.isNotEmpty()) {
            _waste.addCard(_deck.drawCard().apply { faceUp = true })
            _moves.value++
            appendHistory()
        } else if (_waste.pile.isNotEmpty()) {
            // add back all cards from waste to deck
            _deck.replace(_waste.pile.toMutableList())
            _waste.resetCards()
            _moves.value++
            appendHistory()
        }
    }

    // runs when user taps on waste
    fun onWasteTap() {
        _waste.let {
            if (it.pile.isNotEmpty()) {
                // if any move is possible then remove card from waste
                if (legalMove(listOf(it.pile.last()))) {
                    it.removeCard()
                    appendHistory()
                }
            }
        }
    }

    // runs when user taps on foundation
    fun onFoundationTap(fIndex: Int) {
        val foundation = _foundation[fIndex]
        if (foundation.pile.isNotEmpty()) {
            // if any move is possible then remove card from foundation
            if (legalMove(listOf(foundation.pile.last()))) {
                foundation.removeCard()
                _score.value--
                appendHistory()
            }
        }
    }

    // runs when user taps on tableau
    fun onTableauTap(tableauIndex: Int, cardIndex: Int) {
        val tableauPile = _tableau[tableauIndex]
        val tPile = tableauPile.pile
        if (tPile.isNotEmpty()) {
            // if card clicked is face up and move is possible then remove cards from tableau pile
            if (tPile[cardIndex].faceUp && legalMove(tPile.subList(cardIndex, tPile.size))) {
                tableauPile.removeCards(cardIndex)
                appendHistory()
            }
        }
    }

    /**
     *  Should be called after successful [onWasteTap] or [onTableauTap] since game can only end
     *  after one of those clicks and if each foundation pile has exactly 13 Cards.
     */
    private fun gameWon(): Boolean {
        foundation.forEach { if (it.pile.size != 13) return false }
        return true
    }

    /**
     *  Checks if move is possible by attempting to add [cards] to piles. Returns true if added.
     */
    private fun legalMove(cards: List<Card>): Boolean {
        if (cards.size == 1) {
            _foundation.forEach {
                if (it.addCard(cards.first())) {
                    _moves.value++
                    _score.value++
                    if (gameWon()) _gameWon.value = true
                    return true
                }
            }
        }
        _tableau.forEach {
            if (it.addCards(cards)) {
                _moves.value++
                return true
            }
        }
        return false
    }

    /**
     *  Takes a [Snapshot] of current StateObject values and stores them in a [History] object. We
     *  then immediately dispose of the [Snapshot] to avoid memory leaks.
     */
    private fun recordHistory() {
        val currentSnapshot = Snapshot.takeSnapshot()
        currentSnapshot.enter {
            currentStep = History(
                score = _score.value,
                deck = _deck.gameDeck.toList(),
                waste = _waste.pile.toList(),
                foundation = _foundation.map { it.pile.toList() },
                tableauFaceUp = _tableau.map { it.faceUpCards },
                tableau = _tableau.map { it.pile.toList() }
            )
        }
        currentSnapshot.dispose()
    }

    /**
     *  Adds [currentStep] to our [historyList] list before overwriting [currentStep] using
     *  [recordHistory]. This should be call after every legal move.
     */
    private fun appendHistory() {
        // limit number of undo steps to 15
        if (historyList.size == 15) historyList.removeFirst()
        historyList.add(currentStep)
        recordHistory()
    }

    /**
     *  We pop the last [History] value in [historyList] and use it to restore the game state to it.
     */
    fun undo() {
        if (historyList.isNotEmpty()) {
            val step = historyList.removeLast()
            _score.value = step.score
            _deck.undo(step.deck)
            _waste.undo(step.waste)
            _foundation.forEachIndexed { i, foundation ->
                foundation.undo(step.foundation[i])
            }
            _tableau.forEachIndexed { i, tableau ->
                tableau.undoFaceUpCards(step.tableauFaceUp[i])
                tableau.undo(step.tableau[i])
            }
            // called to ensure currentStep stays updated.
            recordHistory()
            // counts as a move still
            _moves.value++
        }
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