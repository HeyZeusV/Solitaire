package com.heyzeusv.solitaire.board.piles

import com.heyzeusv.solitaire.util.Suits

// card value to their display value
val cardsMap = mapOf(
    0 to "A",
    1 to "2",
    2 to "3",
    3 to "4",
    4 to "5",
    5 to "6",
    6 to "7",
    7 to "8",
    8 to "9",
    9 to "10",
    10 to "J",
    11 to "Q",
    12 to "K"
)

/**
 *  Contains information for an individual [PlayingCard].
 *  
 *  @property value The value of the card in the form of an [Int].
 *  @property suit One of the 4 possible values from [Suits].
 *  @property faceUp Determines which side is displayed to user.
 */
data class Card(val value: Int, val suit: Suits, val faceUp: Boolean = false) {

    /**
     *  @return The value of the card in the form of a [String] using [cardsMap].
     */
    fun getDisplayValue(): String = cardsMap[value] ?: "A"

    override fun toString(): String = if (faceUp) "${cardsMap[value]} of ${suit.name}" else "???"
}