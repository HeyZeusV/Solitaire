package com.heyzeusv.solitaire

/**
 *  Game board information.
 */
class Board {

    val deck = Deck()
    // creates Foundation for each Suit
    val foundations = Suits.entries.map { Foundation(it) }

}