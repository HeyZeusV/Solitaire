package com.heyzeusv.solitaire

/**
 *  Class that handles 52 [Card] deck creation.
 */
class Deck {

    private val baseDeck = MutableList(52) { Card(it % 13, getSuit(it)) }
    private var _gameDeck: MutableList<Card> = mutableListOf()
    val gameDeck: List<Card> get() = _gameDeck

    // removes the first card from gameDeck and returns it
    fun drawCard(): Card = _gameDeck.removeFirst()

    // replace gameDeck with given list
    fun replace(list: MutableList<Card>) {
        _gameDeck = list
    }

    // reset gameDeck and shuffle the cards
    fun reset() {
        replace(baseDeck)
        _gameDeck.shuffle()
    }

    /**
     *  Used during creation of deck to assign suit to each card.
     *  Cards  0-12 -> Clubs
     *  Cards 13-25 -> Diamonds
     *  Cards 26-38 -> Hearts
     *  Cards 39-51 -> Spades
     */
    private fun getSuit(i: Int) = when (i / 13) {
        0 -> Suits.CLUBS
        1 -> Suits.DIAMONDS
        2 -> Suits.HEARTS
        else -> Suits.SPADES
    }

    init {
        _gameDeck = baseDeck
    }
}
