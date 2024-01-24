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

    private val _deck = Deck()
    val deck: Deck get() = _deck

    private val _foundation = Suits.entries.map { Foundation(it) }.toMutableList()
    val foundation: List<Foundation> get() = _foundation

    private val _tableau = MutableList(7) { Tableau() }
    val tableau: List<Tableau> get() = _tableau

    private val _waste = Waste()
    val waste: Waste get() = _waste

    private val _gameWon = MutableStateFlow(false)
    val gameWon: StateFlow<Boolean> get() = _gameWon

    // goes through all card piles in the game and resets them for a new game
    fun reset() {
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
        _gameWon.value = false
    }

    // runs when user taps on deck
    fun onDeckTap() {
        // add card to waste if deck is not empty and flip it face up
        if (_deck.gameDeck.isNotEmpty()) {
            _waste.addCard(_deck.drawCard().apply { faceUp = true })
        } else {
            // add back all cards from waste to deck
            _deck.replace(_waste.pile.toMutableList())
            _waste.resetCards()
        }
    }

    // runs when user taps on waste
    fun onWasteTap() {
        _waste.let {
            if (it.pile.isNotEmpty()) {
                // if any move is possible then remove card from waste
                if (legalMove(listOf(it.pile.last()))) it.removeCard()
                if (gameWon()) _gameWon.value = true
            }
        }
    }

    // runs when user taps on foundation
    fun onFoundationTap(fIndex: Int) {
        val foundation = _foundation[fIndex]
        if (foundation.pile.isNotEmpty()) {
            // if any move is possible then remove card from foundation
            if (legalMove(listOf(foundation.pile.last()))) foundation.removeCard()
        }
    }

    // runs when user taps on tableau
    fun onTableauTap(tableauIndex: Int, cardIndex: Int) {
        val tableauPile = _tableau[tableauIndex]
        val tPile = tableauPile.pile
        if (tPile.isNotEmpty()) {
            if (tPile[cardIndex].faceUp && legalMove(tPile.subList(cardIndex, tPile.size))) {
                tableauPile.removeCards(cardIndex)
                if (gameWon()) _gameWon.value = true
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
            _foundation.forEach { if (it.addCard(cards.first())) return true }
        }
        _tableau.forEach {
            if (it.addCards(cards)) return true
        }
        return false
    }
}