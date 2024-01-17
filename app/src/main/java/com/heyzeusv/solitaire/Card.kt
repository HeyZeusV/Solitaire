package com.heyzeusv.solitaire

/**
 *  Data class containing information for individual cards. [value] ranges from 0 to 13 which
 *  corresponds to the values 1 to 10, A, J, Q, K. [suit] is one of 4 possible values from the
 *  [Suits] enum class. [faceUp] determines if the user can see the card.
 */
data class Card(val value: Int, val suit: Suits, var faceUp: Boolean = false)
