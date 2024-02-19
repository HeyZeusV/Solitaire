package com.heyzeusv.solitaire.data.pile.tableau

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.TableauPile
import com.heyzeusv.solitaire.util.Suits

/**
 *  [DifferentColorTableau], apart from the value restraint, require the last card of [mPile] be a
 *  different [Suits] color than the first card of new cards to be added. Users can move cards
 *  between [DifferentColorTableau] piles or move them to a [Foundation] pile in order to reveal
 *  more cards.
 */
abstract class DifferentColorTableau(initialPile: List<Card> = emptyList()) : TableauPile(initialPile) {

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
            // add cards if value of last card of pile is 1 more than first card of new cards
            // and if they are different colors
            if (cFirst.suit.color != pLast.suit.color && cFirst.value == pLast.value - 1) {
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
     *  Reset [mPile] to initial game state.
     */
    abstract override fun reset(cards: List<Card>)

    /**
     *  Used to return [mPile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        mPile.clear()
        _faceDownCards = cards.filter { !it.faceUp }.size
        if (cards.isEmpty()) return
        mPile.addAll(cards)
    }
}