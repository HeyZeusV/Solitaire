package com.heyzeusv.solitaire

/**
 *  Enum class containing the 4 possible suits in a game of Solitaire, with [suit] if needed.
 */
enum class Suits(val suit: String, val color: String) {
    HEARTS("Hearts", "Red"),
    DIAMONDS("Diamonds", "Red"),
    SPADES("Spades", "Black"),
    CLUBS("Clubs", "Black")
}