package com.heyzeusv.solitaire

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 *  Data manager for Board.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
class BoardViewModel : ViewModel() {

    private val _deck = MutableStateFlow(Deck())
    val deck: StateFlow<Deck> get() = _deck

    private val _foundation = MutableStateFlow(Suits.entries.map { Foundation(it) })
    val foundation: StateFlow<List<Foundation>> get() = _foundation

    private val _tableau = MutableStateFlow(MutableList(7) { Tableau() })
    val tableau: StateFlow<List<Tableau>> get() = _tableau

    private val _waste = MutableStateFlow(mutableListOf<Card>())
    val waste: StateFlow<List<Card>> get() = _waste

    // goes through all card piles in the game and resets them for a new game
    fun reset() {
        // shuffled 52 card deck
        _deck.value.reset()
        // empty foundations
        _foundation.value.forEach { it.resetCards() }
        // each pile in the tableau has 1 more card than the previous
        _tableau.value.forEachIndexed { i, _ ->
            val cards = MutableList(i + 1) { _deck.value.drawCard() }
            _tableau.value[i] = Tableau(cards)
        }
        // clear the waste pile
        _waste.value.clear()
    }

    // runs when user taps on deck
    fun onDeckTap() {
        // add card to waste if deck is not empty and flip it face up
        if (_deck.value.gameDeck.isNotEmpty()) {
            _waste.value.add(_deck.value.drawCard().apply { faceUp = true })
        } else {
            // add back all cards from waste to deck
            _deck.value.replace(_waste.value)
            _waste.value.clear()
        }
    }

    // runs when user taps on waste
    fun onWasteTap() {
        _waste.value.let {
            if (it.isNotEmpty()) {
                // if any move is possible then remove card from waste
                if (legalMove(listOf(it.last()))) it.removeLast()
            }
        }
    }

    // runs when user taps on foundation
    fun onFoundationTap(fIndex: Int) {
        val foundation = _foundation.value[fIndex]
        if (foundation.pile.isNotEmpty()) {
            // if any move is possible then remove card from foundation
            if (legalMove(listOf(foundation.pile.last()))) foundation.removeCard()
        }
    }

    // runs when user taps on tableau
    fun onTableauTap(tableauIndex: Int, cardIndex: Int) {
        val tableauPile = _tableau.value[tableauIndex]
        val tPile = tableauPile.pile
        if (tPile.isNotEmpty()) {
            if (legalMove(tPile.subList(cardIndex, tPile.size))) tableauPile.removeCards(cardIndex)
        }
    }

    /**
     *  Checks if move is possible by attempting to add [cards] to piles. Returns true if added.
     */
    private fun legalMove(cards: List<Card>): Boolean {
        if (cards.size == 1) {
            _foundation.value.forEach {
                if (it.addCard(cards.first())) return true
            }
        }
        _tableau.value.forEach {
            if (it.addCards(cards)) return true
        }
        return false
    }
}