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
        if (cards.isEmpty()) return false
        val card = cards[0]
        if (card.suit == suit && card.value == pile.size) {
            mPile.add(card)
            return true
        }
        return false
    }

    /**
     *  Removes the last [Card] in [mPile] which would refer to the top showing card and return it.
     */
    override fun remove(tappedIndex: Int): Card = mPile.removeLast()

    /**
     *  Reset [mPile] using given [cards].
     */
    override fun reset(cards: List<Card>) = mPile.clear()

    /**
     *  Used to return [mPile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        mPile.clear()
        if (cards.isEmpty()) return
        mPile.addAll(cards)
    }

    override fun toString(): String = "${suit.name}: ${pile.toList()}"
}