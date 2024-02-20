package com.heyzeusv.solitaire.data.pile

import com.heyzeusv.solitaire.data.Card

/**
 *  In Solitaire, Waste refers to the face up pile where cards drawn from [Stock] are placed. Only
 *  the top [Card] is playable.
 */
class Waste(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    /**
     *  Adds given [cards] to [_pile].
     */
    override fun add(cards: List<Card>): Boolean {
        return _pile.addAll(cards.map { it.copy(faceUp = true) })
    }

    /**
     *  Removes the last [Card] in [_pile] which would refer to the top showing card and return it.
     */
    override fun remove(tappedIndex: Int): Card = _pile.removeLast()

    /**
     *  Reset [_pile] using given [cards].
     */
    override fun reset(cards: List<Card>) = _pile.clear()

    /**
     *  Used to return [_pile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        _pile.clear()
        if (cards.isEmpty()) return
        // makes sure all cards are face up
        _pile.addAll(cards.map { it.copy(faceUp = true) })
    }
}