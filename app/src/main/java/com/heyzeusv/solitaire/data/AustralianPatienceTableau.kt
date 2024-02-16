package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.util.Suits

/**
 *  In Australian Patience, Tableau refers to the 7 piles that start with 4 face up cards per [mPile]
 *  and no face down cards. Users can move cards between [AustralianPatienceTableau] piles or move
 *  them to a [Foundation] pile in order to access covered cards.
 */
class AustralianPatienceTableau(initialPile: List<Card> = emptyList()) : TableauPile(initialPile) {

    /**
     *  Attempts to add given [cards] to [mPile] depending on [cards] first card value and suit and
     *  [mPile]'s last card value and suit. Returns true if added.
     */
    override fun add(cards: List<Card>): Boolean {
        if (cards.isEmpty()) return false

        val cFirst = cards.first()
        // can't add a card to its own pile
        if (mPile.contains(cFirst)) return false
        if (pile.isNotEmpty()) {
            val pLast = pile.last()
            // add cards if value of last card of pile is 1 more than first card of given cards and
            // if they are the same suit
            if (cFirst.value == pLast.value - 1 && cFirst.suit == pLast.suit) {
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
     *  Removes all cards from [mPile] starting from [tappedIndex] to the end of [mPile].
     */
    override fun remove(tappedIndex: Int): Card {
        mPile.subList(tappedIndex, mPile.size).clear()
        // return value isn't used
        return Card(0, Suits.SPADES, false)
    }

    /**
     *  Resets [mPile] by clearing existing cards, adding given [cards], and ensuring all cards are
     *  face up.
     */
    override fun reset(cards: List<Card>) {
        mPile.apply {
            clear()
            val faceUpCards = cards.map { it.copy(faceUp =  true) }
            addAll(faceUpCards)
        }
    }

    /**
     *  Used to return [mPile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        mPile.clear()
        if (cards.isEmpty()) return
        mPile.addAll(cards)
    }
}