package com.heyzeusv.solitaire

// card value to their display value
val cardsMap = mapOf(0 to "A", 1 to "2", 2 to "3" , 3 to "4", 4 to "5", 5 to "6", 6 to "7", 7 to "8", 8 to "9", 9 to "10", 10 to "J", 11 to "Q", 12 to "K")
/**
 *  Data class containing information for individual cards. [value] ranges from 0 to 13 which
 *  corresponds to the values 1 to 10, A, J, Q, K. [suit] is one of 4 possible values from the
 *  [Suits] enum class. [faceUp] determines if the user can see the card.
 */
data class Card(val value: Int, val suit: Suits, var faceUp: Boolean = false) {

    override fun toString(): String = if (faceUp) "${cardsMap[value]} of ${suit.suit}" else "???"
}
