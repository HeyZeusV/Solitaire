package com.heyzeusv.solitaire.board.piles

import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.Suits

/**
 *  In Solitaire, Foundation refers to the pile where players have to build up a specific [suit]
 *  from Ace to King.
 */
class Foundation(
    val suit: Suits,
    val gamePile: GamePiles,
    initialPile: List<CardLogic> = emptyList()
) : Pile(initialPile) {

    /**
     *  Adds first card of given [cards] to [truePile].
     */
    override fun add(cards: List<CardLogic>) {
        _truePile.add(cards.first().copy(faceUp = true))
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
    }

    /**
     *  Adds all given [cards] to [truePile]
     */
    fun addAll(cards: List<CardLogic>) {
        _truePile.addAll(cards)
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
    }

    /**
     *  Removes the last [CardLogic] in [truePile] which would refer to the top showing card and
     *  returns it.
     */
    override fun remove(tappedIndex: Int): CardLogic {
        val removedCard = _truePile.removeLast()
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
        return removedCard
    }

    /**
     *  Reset [truePile] using given [cards].
     */
    override fun reset(cards: List<CardLogic>) {
        animatedPiles.clear()
        resetHistory()
        _truePile.clear()
        _displayPile.clear()
        currentStep = cards
        if (cards.isNotEmpty()) {
            val flippedCards = cards.map { it.copy(faceUp = true) }
            _truePile.addAll(flippedCards)
            _displayPile.addAll(flippedCards)
            currentStep = flippedCards
        }
    }

    /**
     *  Used to return [truePile] to a previous state.
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