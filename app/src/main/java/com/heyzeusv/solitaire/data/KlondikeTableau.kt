package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.util.Suits

/**
 *  In Klondike Solitaire, Tableau refers to the 7 piles that start with 1 face up card per [mPile]
 *  and the rest face down. Users can move cards between [KlondikeTableau] piles or move them to a
 *  [Foundation] pile in order to reveal more cards.
 */
class KlondikeTableau(initialPile: List<Card> = emptyList()) : TableauPile(initialPile) {

    // keeps track of number of face down cards in mPile
    private var _faceDownCards: Int = 0
    val faceDownCards: Int get() = _faceDownCards

    /**
     *  Attempts to add given [cards] to [mPile] depending on [cards] first card value and suit and
     *  [mPile]'s last card value and suit. Returns true if added.
     */
    override fun add(cards: List<Card>): Boolean {
        if (cards.isEmpty()) return false

        val cFirst = cards.first()
        if (pile.isNotEmpty()) {
            val pLast = pile.last()
            // add cards if value of last card of pile is 1 more than first card of new cards
            // and if they are different colors
            if (cFirst.value == pLast.value - 1 && cFirst.suit.color != pLast.suit.color) {
                mPile.addAll(cards)
                return true
            }
        // add cards if pile is empty and first card of given cards is the highest value (King)
        } else if (cFirst.value == 12) {
            mPile.addAll(cards)
            return true
        }
        return false
    }

    /**
     *  Removes all cards from [mPile] starting from [tappedIndex] to the end of [mPile] and flips
     *  the last card if any.
     */
    override fun remove(tappedIndex: Int): Card {
        mPile.subList(tappedIndex, mPile.size).clear()
        // flip the last card up
        if (mPile.isNotEmpty() && !mPile.last().faceUp) {
            mPile[mPile.size - 1] = mPile.last().copy(faceUp = true)
            _faceDownCards--
        }
        // return value isn't used
        return Card(0, Suits.SPADES, false)
    }

    /**
     *  Resets [mPile] by clearing existing cards, adding given [cards], and flipping last card
     *  face up.
     */
    override fun reset(cards: List<Card>) {
        mPile.apply {
            clear()
            addAll(cards)
            this[this.size - 1] = this.last().copy(faceUp = true)
            _faceDownCards = cards.size - 1
        }
    }

    /**
     *  Used to return [mPile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        mPile.clear()
        _faceDownCards = cards.filter { !it.faceUp }.size
        if (cards.isEmpty()) return
        mPile.addAll(cards)
    }

    /**
     *  Used to determine if game could be auto completed by having all face up cards
     */
    fun allFaceUp(): Boolean = _faceDownCards == 0
}