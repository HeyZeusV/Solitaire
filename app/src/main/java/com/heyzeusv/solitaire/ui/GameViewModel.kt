package com.heyzeusv.solitaire.ui

import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.Foundation
import com.heyzeusv.solitaire.data.PileHistory
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.Stock
import com.heyzeusv.solitaire.data.Tableau
import com.heyzeusv.solitaire.data.Waste
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.MoveResult.MOVE
import com.heyzeusv.solitaire.util.MoveResult.MOVE_SCORE
import com.heyzeusv.solitaire.util.MoveResult.ILLEGAL
import com.heyzeusv.solitaire.util.MoveResult.MOVE_MINUS_SCORE
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.isNotEqual
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  Data manager for game.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    private val ss: ShuffleSeed
) : ViewModel() {

    // holds all 52 playing Cards
    private var baseDeck = MutableList(52) { Card(it % 13, getSuit(it)) }
    private var shuffledDeck = emptyList<Card>()

    private val _stock = Stock()
    val stock: Stock get() = _stock

    private val _waste = Waste()
    val waste: Waste get() = _waste

    private val _stockWasteEmpty = MutableStateFlow(false)
    val stockWasteEmpty: StateFlow<Boolean> get() = _stockWasteEmpty

    private val _foundation = Suits.entries.map { Foundation(it) }.toMutableList()
    val foundation: List<Foundation> get() = _foundation

    private val _tableau = MutableList(7) { Tableau() }
    val tableau: List<Tableau> get() = _tableau

    private val _historyList = mutableListOf<PileHistory>()
    val historyList: List<PileHistory> get() = _historyList
    private lateinit var currentStep: PileHistory

    private val _undoEnabled = MutableStateFlow(false)
    val undoEnabled: StateFlow<Boolean> get() = _undoEnabled

    private val _autoCompleteActive = MutableStateFlow(false)
    val autoCompleteActive: StateFlow<Boolean> get() = _autoCompleteActive

    private var _autoCompleteCorrection: Int = 0
    val autoCompleteCorrection: Int get() = _autoCompleteCorrection

    private val _gameWon = MutableStateFlow(false)
    val gameWon: StateFlow<Boolean> get() = _gameWon

    /**
     *  Goes through all the card piles in the game and resets them for either the same game or a
     *  new game depending on [resetOption].
     */
    fun reset(resetOption: ResetOptions) {
        when (resetOption) {
            ResetOptions.RESTART -> _stock.reset(shuffledDeck)
            ResetOptions.NEW -> {
                shuffledDeck = baseDeck.shuffled(ss.shuffleSeed)
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
        _undoEnabled.value = false
        recordHistory()
        _gameWon.value = false
        _autoCompleteActive.value = false
        _autoCompleteCorrection = 0
        _stockWasteEmpty.value = false
    }

    /**
     *  Runs when user taps on Stock pile. Either draws Card(s) from Stock if any or resets Stock by
     *  adding Cards back from Waste. [drawAmount] will be used for testing and has default parameter
     *  that will be updated depending on game selected.
     */
    fun onStockClick(drawAmount: Int): MoveResult {
        // add card to waste if stock is not empty and flip it face up
        if (_stock.pile.isNotEmpty()) {
            _waste.add(_stock.removeMany(drawAmount))
            appendHistory()
            _stockWasteEmpty.value = _waste.pile.size <= 1 && _stock.pile.isEmpty()
            return MOVE
        } else if (_waste.pile.size > 1) {
            // add back all cards from waste to stock
            _stock.add(_waste.pile.toList())
            _waste.reset()
            appendHistory()
            return MOVE
        }
        return ILLEGAL
    }

    /**
     *  Runs when user taps on Waste pile. Checks to see if top [Card] can be moved to any other
     *  pile except [Stock]. If so, it is removed from [Waste].
     */
     fun onWasteClick(): MoveResult {
        _waste.let {
            if (it.pile.isNotEmpty()) {
                val result = legalMove(listOf(it.pile.last()))
                // if any move is possible then remove card from waste
                if (result != ILLEGAL) {
                    it.remove()
                    appendHistory()
                    autoComplete()
                    _stockWasteEmpty.value = it.pile.size <= 1 && _stock.pile.isEmpty()
                    return result
                }
            }
        }
        return ILLEGAL
    }

    /**
     *  Runs when user taps on Foundation with given [fIndex]. Checks to see if top [Card] can be
     *  moved to any [Tableau] pile. If so, it is removed from [_foundation] with given [fIndex].
     */
    fun onFoundationClick(fIndex: Int): MoveResult {
        val foundation = _foundation[fIndex]
        if (foundation.pile.isNotEmpty()) {
            val result = legalMove(listOf(foundation.pile.last()))
            // if any move is possible then remove card from foundation
            if (result != ILLEGAL) {
                foundation.remove()
                appendHistory()
                // legalMove() doesn't detect removal from Foundation which always results in losing
                // score.
                return MOVE_MINUS_SCORE
            }
        }
        return ILLEGAL
    }

    /**
     *  Runs when user taps on Tableau pile with given [tableauIndex] and given [cardIndex]. Checks
     *  to see if tapped sublist of [Card]s can be moved to another [Tableau] pile or a [Foundation].
     */
    fun onTableauClick(tableauIndex: Int, cardIndex: Int): MoveResult {
        val tableauPile = _tableau[tableauIndex]
        val tPile = tableauPile.pile
        if (tPile.isNotEmpty() && tPile[cardIndex].faceUp) {
            val result = legalMove(tPile.subList(cardIndex, tPile.size))
            // if card clicked is face up and move is possible then remove cards from tableau pile
            if (result != ILLEGAL) {
                tableauPile.remove(cardIndex)
                appendHistory()
                autoComplete()
                return result
            }
        }
        return ILLEGAL
    }

    /**
     *  Checks if Stock and Waste are empty and that all Cards in Tableau are face up. If so, then
     *  go through each Tableau and call onTableauClick on the last card. This is repeated until
     *  the game is completed.
     */
    private fun autoComplete() {
        if (_autoCompleteActive.value) return
        if (_stock.pile.isEmpty() && _waste.pile.isEmpty()) {
            _tableau.forEach { if (!it.allFaceUp()) return }
            viewModelScope.launch {
                _autoCompleteActive.value = true
                _autoCompleteCorrection = 0
                while (!gameWon()) {
                    _tableau.forEachIndexed { i, tableau ->
                        if (tableau.pile.isEmpty()) return@forEachIndexed
                        onTableauClick(i, tableau.pile.size - 1)
                        delay(100)
                    }
                }
            }
        }
    }

    /**
     *  Should be called after successful [onWasteClick] or [onTableauClick] since game can only end
     *  after one of those clicks and if each foundation pile has exactly 13 Cards.
     */
    private fun gameWon(): Boolean {
        foundation.forEach { if (it.pile.size != 13) return false }
//        _moves.value = _moves.value - autoCompleteMoveCorrection
        _autoCompleteActive.value = false
        _gameWon.value = true
        return true
    }

    /**
     *  Checks if move is possible by attempting to add [cards] to piles. Returns true if added.
     */
    private fun legalMove(cards: List<Card>): MoveResult {
        if (cards.size == 1) {
            _foundation.forEach {
                if (it.add(cards)) {
                    _autoCompleteCorrection++
                    return MOVE_SCORE
                }
            }
        }
        _tableau.forEach { if (it.add(cards)) return MOVE }
        return ILLEGAL
    }

    /**
     *  Takes a [Snapshot] of current StateObject values and stores them in a [PileHistory] object. We
     *  then immediately dispose of the [Snapshot] to avoid memory leaks.
     */
    private fun recordHistory() {
        val currentSnapshot = Snapshot.takeMutableSnapshot()
        currentSnapshot.enter {
            currentStep = PileHistory(
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
        _undoEnabled.value = true
        recordHistory()
    }

    /**
     *  We pop the last [PileHistory] value in [_historyList] and use it to restore the game
     *  state to it.
     */
    fun undo() {
        if (_historyList.isNotEmpty()) {
            val step = _historyList.removeLast()
            if (_historyList.isEmpty()) _undoEnabled.value = false
            if (_stock.pile.isNotEqual(step.stock.pile)) {
                _stock.undo(step.stock.pile)
                _stockWasteEmpty.value = _waste.pile.size <= 1 && _stock.pile.isEmpty()
            }
            if (_waste.pile.isNotEqual(step.waste.pile)) {
                _waste.undo(step.waste.pile)
                _stockWasteEmpty.value = _waste.pile.size <= 1 && _stock.pile.isEmpty()
            }
            _foundation.forEachIndexed { i, foundation ->
                if (foundation.pile.isNotEqual(step.foundation[i].pile)) {
                    foundation.undo(step.foundation[i].pile)
                }
            }
            _tableau.forEachIndexed { i, tableau ->
                if (tableau.pile.isNotEqual(step.tableau[i].pile)) {
                    tableau.undo(step.tableau[i].pile)
                }
            }
            // called to ensure currentStep stays updated.
            recordHistory()
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

    init {
        reset(ResetOptions.NEW)
    }
}