package com.heyzeusv.solitaire.board.piles

import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.Suits

/**
 *  In Solitaire, the Foundation refers to the piles where the players have to build up from Ace
 *  to King for a specific suit.
 *
 *  @property suit One of the 4 possible values from [Suits].
 *  @property gamePile Used by animations to determine position on screen.
 *  @param initialPile Cards that [Pile] is initialized with.
 */
class Foundation(
    val suit: Suits,
    val gamePile: GamePiles,
    initialPile: List<Card> = emptyList()
) : Pile(initialPile) {

    /**
     *  Adds a [Card], face up, to the end [truePile].
     *
     *  @param card The [Card] to be added.
     */
    override fun add(card: Card) {
        _truePile.add(card.copy(faceUp = true))
        animatedPiles.add(_truePile.toList())
        appendHistory()
    }

    /**
     *  Adds multiple [Cards][Card], all face up, to the end [truePile].
     *
     *  @param cards The [Cards][Card] to be added.
     */
    override fun addAll(cards: List<Card>) {
        _truePile.addAll(cards.map { it.copy(faceUp = true) })
        animatedPiles.add(_truePile.toList())
        appendHistory()
    }

    /**
     *  Removes the last [Card] in [truePile], which would refer to the top showing [Card].
     *
     *  @param tappedIndex Not used in this version.
     *  @return The last [Card] of [truePile] or the Ace of Diamonds if empty. (Not Used)
     */
    override fun remove(tappedIndex: Int): Card {
        return if (_truePile.isEmpty()) {
            Card(0, Suits.DIAMONDS)
        } else {
            val removedCard = _truePile.removeLast()
            animatedPiles.add(_truePile.toList())
            appendHistory()
            return removedCard
        }
    }

    /**
     *  Resets [animatedPiles] and [historyList]. Replaces [truePile] and [displayPile] with a new
     *  list of face up [Cards][Card].
     *
     *  @param cards The new list of [Cards][Card].
     */
    override fun reset(cards: List<Card>) {
        resetLists()
        currentStep = cards
        if (cards.isNotEmpty()) {
            val flippedCards = cards.map { it.copy(faceUp = true) }
            _truePile.addAll(flippedCards)
            _displayPile.addAll(flippedCards)
            currentStep = flippedCards
        }
    }

    /**
     *  Returns [truePile] to its previous state by accessing [historyList].
     */
    override fun undo() {
        _truePile.clear()
        val history = retrieveHistory()
        _truePile.addAll(history)
        animatedPiles.add(_truePile.toList())
        currentStep = _truePile.toList()
    }

    override fun toString(): String = "${suit.name}: ${truePile.toList()}"
}