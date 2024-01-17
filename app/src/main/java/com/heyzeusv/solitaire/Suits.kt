package com.heyzeusv.solitaire

/**
 *  Enum class containing the 4 possible suits in a game of Solitaire, with [suit] if needed.
 */
enum class Suits(val suit: String, val color: String) {
    CLUBS("Clubs", "Black"),
    DIAMONDS("Diamonds", "Red"),
    HEARTS("Hearts", "Red"),
    SPADES("Spades", "Black")
}