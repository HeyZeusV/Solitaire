package com.heyzeusv.solitaire.data.pile

import com.heyzeusv.solitaire.data.Card

/**
 *  In Solitaire, Stock refers to the face down pile where players draw from and place the drawn
 *  card on the [Waste] pile.
 */
class Stock(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    /**
     *  Removes [amount] given and returns them as a list.
     */
    fun removeMany(amount: Int): List<Card> {
        val list = mutableListOf<Card>()
        for (i in 1..amount) {
            try {
                list.add(remove())
            } catch (e: NoSuchElementException) {
                return list
            }
        }
        return list
    }

    /**
     *  Add given [cards] to [_pile].
     */
    override fun add(cards: List<Card>): Boolean {
        return _pile.addAll(cards.map { it.copy(faceUp = false) })
    }

    /**
     *  Remove the first [Card] in [_pile] and return it.
     */
    override fun remove(tappedIndex: Int): Card = _pile.removeFirst()

    /**
     *  Reset [_pile] using given [cards].
     */
    override fun reset(cards: List<Card>) {
        _pile.clear()
        add(cards)
    }

    /**
     *  Used to return [_pile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        _pile.clear()
        if (cards.isEmpty()) return
        // only last card is shown to user, this makes sure it is not visible
        val mutableCards = cards.toMutableList()
        mutableCards[mutableCards.size - 1] = mutableCards.last().copy(faceUp = false)
        _pile.addAll(mutableCards)
    }
}