package com.heyzeusv.solitaire.data.pile.tableau

import com.heyzeusv.solitaire.data.Card

/**
 *  Tableau used by Yukon.
 */
class YukonTableau(initialPile: List<Card> = emptyList()) : DifferentColorTableau(initialPile) {

    /**
     *  Resets [mPile] by clearing existing cards, adding given [cards], and flipping last 5 cards
     *  face up.
     */
    override fun reset(cards: List<Card>) {
        mPile.apply {
            clear()
            addAll(cards)
            if (cards.size > 1) {
                // flip last 5 cards face up
                for (i in cards.size.downTo(cards.size - 4)) {
                    this[i - 1] = this[i - 1].copy(faceUp =  true)
                }
                _faceDownCards = cards.size - 5
            } else {
                // flip single card face up
                this[this.size - 1] = this.last().copy(faceUp = true)
            }
        }
    }
}