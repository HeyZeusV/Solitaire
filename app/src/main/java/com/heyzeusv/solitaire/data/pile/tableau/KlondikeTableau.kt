package com.heyzeusv.solitaire.data.pile.tableau

import com.heyzeusv.solitaire.data.Card

/**
 *  Tableau used by Klondike.
 */
class KlondikeTableau(initialPile: List<Card> = emptyList()) : DifferentColorTableau(initialPile) {

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
}