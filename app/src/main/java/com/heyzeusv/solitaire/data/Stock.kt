package com.heyzeusv.solitaire.data

/**
 *  In Solitaire, Stock refers to the face down pile where players draw from and place the drawn
 *  card on the [Waste] pile.
 */
class Stock(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    /**
     *  Add given [cards] to [mPile].
     */
    override fun add(cards: List<Card>): Boolean {
        return mPile.addAll(cards.map { it.copy(faceUp = false) })
    }

    /**
     *  Remove the first [Card] in [mPile] and return it.
     */
    override fun remove(tappedIndex: Int): Card = mPile.removeFirst()

    /**
     *  Reset [mPile] using given [cards].
     */
    override fun reset(cards: List<Card>) {
        mPile.clear()
        add(cards)
    }

    /**
     *  Used to return [mPile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        mPile.clear()
        if (cards.isEmpty()) return
        // only last card is shown to user, this makes sure it is not visible
        val mutableCards = cards.toMutableList()
        mutableCards[mutableCards.size - 1] = mutableCards.last().copy(faceUp = false)
        mPile.addAll(mutableCards)
    }

    override fun toString(): String = pile.toList().toString()
}