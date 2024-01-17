package com.heyzeusv.solitaire

/**
 *  In Solitaire, Tableau refers to the 7 piles that start with 1 face up card per pile and the rest
 *  face down. Users can move cards between [Tableau] piles or move them to a [Foundation] pile in
 *  order to reveal more cards.
 */
class Tableau(private val _pile: MutableList<Card> = mutableListOf()) {

    val pile: List<Card> get() = _pile

    /**
     *  Attempts to add given [cards] to [pile] depending on [cards] first card value and suit and
     *  [pile]'s last card value and suit.
     */
    fun addCards(cards: List<Card>) {
        if (cards.isEmpty()) return
        val cFirst = cards.first()
        if (pile.isNotEmpty()) {
            val pLast = pile.last()
            // add cards if last card of pile is 1 more than first card of new cards
            // and if they are different colors
            if (cFirst.value == pLast.value - 1 && cFirst.suit.color != pLast.suit.color) {
                _pile.addAll(cards)
            }
        // add cards if pile is empty and first card of new cards is the highest value
        } else if (cFirst.value == 12) {
            _pile.addAll(cards)
        }
    }

    /**
     *  Removes all cards from [pile] started from [tappedIndex] to the end of [pile] and flips the
     *  last card if any.
     */
    fun removeCards(tappedIndex: Int) {
        _pile.subList(tappedIndex, _pile.size).clear()
        // flip the last card up
        if (_pile.isNotEmpty()) _pile.last().faceUp = true
    }

    init {
        // flip the last card up
        if (_pile.isNotEmpty()) _pile.last().faceUp = true
    }
}