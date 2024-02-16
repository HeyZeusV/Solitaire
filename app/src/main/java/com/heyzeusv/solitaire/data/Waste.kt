package com.heyzeusv.solitaire.data

/**
 *  In Solitaire, Waste refers to the face up pile where cards drawn from [Stock] are placed. Only
 *  the top [Card] is playable.
 */
class Waste(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    /**
     *  Adds given [cards] to [mPile].
     */
    override fun add(cards: List<Card>): Boolean {
        return mPile.addAll(cards.map { it.copy(faceUp = true) })
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
        // makes sure all cards are face up
        mPile.addAll(cards.map { it.copy(faceUp = true) })
    }
}