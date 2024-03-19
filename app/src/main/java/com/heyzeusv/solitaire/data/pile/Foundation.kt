package com.heyzeusv.solitaire.data.pile

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.util.Suits

/**
 *  In Solitaire, Foundation refers to the pile where players have to build up a specific [suit]
 *  from Ace to King.
 */
class Foundation(val suit: Suits, initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    /**
     *  Adds first card of given [cards] if it matches this [Foundation]'s [suit] and its value
     *  matches what is required next in the sequence. Returns true if added.
     */
    override fun add(cards: List<Card>): Boolean {
        _truePile.add(cards.first())
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
        return true
    }

    fun canAdd(cards:List<Card>): Boolean {
        if (cards.isEmpty()) return false
        val card = cards[0]
        return card.suit == suit && card.value == truePile.size
    }

    /**
     *  Removes the last [Card] in [_truePile] which would refer to the top showing card and return it.
     */
    override fun remove(tappedIndex: Int): Card {
        val removedCard = _truePile.removeLast()
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
        return removedCard
    }

    /**
     *  Reset [_truePile] using given [cards].
     */
    override fun reset(cards: List<Card>) {
        animatedPiles.clear()
        resetHistory()
        _truePile.clear()
        _displayPile.clear()
        currentStep = emptyList()
    }

    /**
     *  Used to return [_truePile] to a previous state.
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