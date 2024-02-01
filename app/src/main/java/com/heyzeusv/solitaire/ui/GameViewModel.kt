package com.heyzeusv.solitaire.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.Foundation
import com.heyzeusv.solitaire.data.History
import com.heyzeusv.solitaire.data.Stock
import com.heyzeusv.solitaire.data.Tableau
import com.heyzeusv.solitaire.data.Waste
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.Suits
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Random

/**
 *  Data manager for game.
 *
 *  [randomSeed] is used in testing in order to get the same shuffle.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
class GameViewModel(private val randomSeed: Long? = null) : ViewModel() {

    // holds all 52 playing Cards
    private var baseDeck = MutableList(52) { Card(it % 13, getSuit(it)) }
    private var shuffledDeck = emptyList<Card>()

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

    private val _stock = Stock()
    val stock: Stock get() = _stock

    private val _waste = Waste()
    val waste: Waste get() = _waste

    private val _foundation = Suits.entries.map { Foundation(it) }.toMutableList()
    val foundation: List<Foundation> get() = _foundation

    private val _tableau = MutableList(7) { Tableau() }
    val tableau: List<Tableau> get() = _tableau

    private val _historyList = mutableStateListOf<History>()
    val historyList: List<History> get() = _historyList
    private lateinit var currentStep: History

    private val _gameWon = MutableStateFlow(false)
    val gameWon: StateFlow<Boolean> get() = _gameWon

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

    fun jobIsCancelled(): Boolean = timerJob?.isCancelled ?: true

    /**
     *  Goes through all the card piles in the game and resets them for either the same game or a
     *  new game depending on [resetOption].
     */
    fun reset(resetOption: ResetOptions) {
        // reset stats
        resetTimer()
        _moves.value = 0
        _score.value = 0
        when (resetOption) {
            ResetOptions.RESTART -> _stock.reset(shuffledDeck)
            ResetOptions.NEW -> {
                shuffledDeck = baseDeck.shuffled(
                    if (randomSeed == null) {
                        Random()
                    } else {
                        Random(randomSeed)
                    }
                )
                _stock.reset(shuffledDeck)
            }
        }
        // empty foundations
        _foundation.forEach { it.reset() }
        // each pile in the tableau has 1 more card than the previous
        _tableau.forEachIndexed { i, tableau ->
            val cards = MutableList(i + 1) { _stock.remove() }
            tableau.reset(cards)
        }
        // clear the waste pile
        _waste.reset()
        _historyList.clear()
        recordHistory()
        _gameWon.value = false
    }

    // runs when user taps on stock
    fun onStockClick() {
        // add card to waste if stock is not empty and flip it face up
        if (_stock.pile.isNotEmpty()) {
            _waste.add(listOf(_stock.remove()))
            _moves.value++
            appendHistory()
        } else if (_waste.pile.isNotEmpty()) {
            // add back all cards from waste to stock
            _stock.add(_waste.pile.toList())
            _waste.reset()
            _moves.value++
            appendHistory()
        }
    }

    // runs when user taps on waste
    fun onWasteClick() {
        _waste.let {
            if (it.pile.isNotEmpty()) {
                // if any move is possible then remove card from waste
                if (legalMove(listOf(it.pile.last()))) {
                    it.remove()
                    appendHistory()
                }
            }
        }
    }

    // runs when user taps on foundation
    fun onFoundationClick(fIndex: Int) {
        val foundation = _foundation[fIndex]
        if (foundation.pile.isNotEmpty()) {
            // if any move is possible then remove card from foundation
            if (legalMove(listOf(foundation.pile.last()))) {
                foundation.remove()
                _score.value--
                appendHistory()
            }
        }
    }

    // runs when user taps on tableau
    fun onTableauClick(tableauIndex: Int, cardIndex: Int) {
        val tableauPile = _tableau[tableauIndex]
        val tPile = tableauPile.pile
        if (tPile.isNotEmpty()) {
            // if card clicked is face up and move is possible then remove cards from tableau pile
            if (tPile[cardIndex].faceUp && legalMove(tPile.subList(cardIndex, tPile.size))) {
                tableauPile.remove(cardIndex)
                appendHistory()
            }
        }
    }

    /**
     *  Should be called after successful [onWasteClick] or [onTableauClick] since game can only end
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
                if (it.add(cards)) {
                    _moves.value++
                    _score.value++
                    if (gameWon()) _gameWon.value = true
                    return true
                }
            }
        }
        _tableau.forEach {
            if (it.add(cards)) {
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
        val currentSnapshot = Snapshot.takeMutableSnapshot()
        currentSnapshot.enter {
            currentStep = History(
                score = _score.value,
                stock = Stock(_stock.pile),
                waste = Waste(_waste.pile),
                foundation = _foundation.map { Foundation(it.suit, it.pile) },
                tableau = _tableau.map { Tableau(it.pile) }
            )
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
     *  We pop the last [History] value in [_historyList] and use it to restore the game state to it.
     */
    fun undo() {
        if (_historyList.isNotEmpty()) {
            val step = _historyList.removeLast()
            _score.value = step.score
            _stock.undo(step.stock.pile)
            _waste.undo(step.waste.pile)
            _foundation.forEachIndexed { i, foundation -> foundation.undo(step.foundation[i].pile) }
            _tableau.forEachIndexed { i, tableau -> tableau.undo(step.tableau[i].pile) }
            // called to ensure currentStep stays updated.
            recordHistory()
            // counts as a move still
            _moves.value++
        }
    }

    /**
     *  Used during creation of deck to assign suit to each card.
     *  Cards  0-12 -> Clubs
     *  Cards 13-25 -> Diamonds
     *  Cards 26-38 -> Hearts
     *  Cards 39-51 -> Spades
     */
    private fun getSuit(i: Int) = when (i / 13) {
        0 -> Suits.CLUBS
        1 -> Suits.DIAMONDS
        2 -> Suits.HEARTS
        else -> Suits.SPADES
    }

    override fun onCleared() {
        super.onCleared()
        // cancel coroutine
        timerJob?.cancel()
    }

    init {
        reset(ResetOptions.NEW)
    }
}