package com.heyzeusv.solitaire

import androidx.compose.runtime.mutableStateListOf

/**
 *  In Solitaire, Foundation refers to the pile where players have to build up a specific [suit]
 *  from Ace to King.
 */
class Foundation(val suit: Suits) : Pile {

    private val _pile = mutableStateListOf<Card>()
    override val pile: List<Card> get() = _pile

    /**
     *  Adds first card of given [cards] if it matches this [Foundation]'s [suit] and its value
     *  matches what is required next in the sequence. Returns true if added.
     */
    override fun add(cards: List<Card>): Boolean {
        if (cards.isEmpty()) return false
        val card = cards[0]
        if (card.suit == suit && card.value == pile.size) {
            _pile.add(card)
            return true
        }
        return false
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
        _pile.addAll(cards)
    }

    override fun toString(): String = "${suit.suit}: ${pile.toList()}"
}