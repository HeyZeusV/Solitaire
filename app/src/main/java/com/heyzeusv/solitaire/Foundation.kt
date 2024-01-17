package com.heyzeusv.solitaire

/**
 *  In Solitaire, Foundation refers to the pile where players have to build up a specific [suit]
 *  from Ace to King.
 */
class Foundation(val suit: Suits) {

    private val _pile = mutableListOf<Card>()
    val pile: List<Card> get() = _pile

    /**
     *  Adds given [card] if it matches this [Foundation]'s [suit] and its value matches what is
     *  required next in the sequence
     */
    fun addCard(card: Card) {
        if (card.suit == suit && card.value == pile.size) _pile.add(card)
    }

    /**
     *  Removes the last card of the pile which would refer to the top card.
     */
    fun removeCard() {
        _pile.removeLast()
    }

    /**
     *  Removes all cards from the pile.
     */
    fun resetCards() {
        _pile.clear()
    }
}