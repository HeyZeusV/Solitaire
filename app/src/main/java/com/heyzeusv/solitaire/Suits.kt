package com.heyzeusv.solitaire

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

/**
 *  Enum class containing the 4 possible suits in a game of Solitaire, with [suit] if needed.
 */
enum class Suits(val suit: String, val color: Color, @DrawableRes val icon: Int) {
    CLUBS("Clubs", Color.Black, R.drawable.suit_club),
    DIAMONDS("Diamonds", Color.Red, R.drawable.suit_diamond),
    HEARTS("Hearts", Color.Red, R.drawable.suit_heart),
    SPADES("Spades", Color.Black, R.drawable.suit_spade)
}