package com.heyzeusv.solitaire

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

/**
 *  Enum class containing the 4 possible suits in a game of Solitaire with additional information.
 *  Includes [suit] in String form, [color] of Suit, drawable id of its [icon], and drawable id
 *  of [emptyIcon] which is shown when its [Foundation] pile is empty.
 */
enum class Suits(
    val suit: String,
    val color: Color,
    @DrawableRes val icon: Int,
    @DrawableRes val emptyIcon: Int
) {
    CLUBS("Clubs", Color.Black, R.drawable.suit_club, R.drawable.foundation_club_empty),
    DIAMONDS("Diamonds", Color.Red, R.drawable.suit_diamond, R.drawable.foundation_diamond_empty),
    HEARTS("Hearts", Color.Red, R.drawable.suit_heart, R.drawable.foundation_heart_empty),
    SPADES("Spades", Color.Black, R.drawable.suit_spade, R.drawable.foundation_spade_empty)
}

/**
 *  Enum class containing both possibilities the user has when resetting the game.
 */
enum class ResetOptions {
    RESTART,
    NEW
}

enum class Games(val gameName: String) {
    KLONDIKETURNONE("Klondike (Turn One)")
}