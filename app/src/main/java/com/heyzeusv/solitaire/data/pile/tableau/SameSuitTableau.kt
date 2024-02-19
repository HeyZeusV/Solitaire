package com.heyzeusv.solitaire.data.pile.tableau

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.pile.TableauPile
import com.heyzeusv.solitaire.util.Suits

/**
 *  [SameSuitTableau], apart from the value restraint, require the last card of [mPile] be the same
 *  [Suits] as the first card of new cards to be added. Users can move cards between
 *  [SameSuitTableau] piles or move them to a [Foundation] pile in order to access covered cards.
 */
class SameSuitTableau(initialPile: List<Card> = emptyList()) : TableauPile(initialPile) {

    // keeps track of number of different Suits in mPile
    private var _suitTypes: Int = 0

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
            if (cFirst.suit == pLast.suit && cFirst.value == pLast.value - 1) {
                mPile.addAll(cards)
                _suitTypes = mPile.map { it.suit }.distinct().size
                return true
            }
        // add cards if pile is empty and first card of given cards is the highest value (King)
        } else if (cFirst.value == 12) {
            mPile.addAll(cards)
            _suitTypes = mPile.map { it.suit }.distinct().size
            return true
        }
        return false
    }

    /**
     *  Removes all cards from [mPile] starting from [tappedIndex] to the end of [mPile].
     */
    override fun remove(tappedIndex: Int): Card {
        mPile.subList(tappedIndex, mPile.size).clear()
        _suitTypes = mPile.map { it.suit }.distinct().size
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
            _suitTypes = map { it.suit }.distinct().size
        }
    }

    /**
     *  Used to return [mPile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        mPile.clear()
        _suitTypes = cards.map { it.suit }.distinct().size
        if (cards.isEmpty()) return
        mPile.addAll(cards)
    }

    fun isMultiSuit(): Boolean = _suitTypes > 1

    /**
     *  It is possible for pile to be same suit, but out of order. This ensures that pile is in
     *  order, this way autocomplete will not be stuck in an infinite loop
     */
    fun inOrder(): Boolean {
        val it = mPile.iterator()
        if (!it.hasNext()) return true
        var current = it.next()
        while (true) {
            if (!it.hasNext()) return true
            val next = it.next()
            if (current.value < next.value) return false
            current = next
        }
    }
}