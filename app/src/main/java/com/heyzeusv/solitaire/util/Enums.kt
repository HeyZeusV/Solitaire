package com.heyzeusv.solitaire.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.heyzeusv.solitaire.R

/**
 *  Enum class containing the 4 possible suits in a game of Solitaire with additional information.
 *  Includes [suit] in String Resource form, [color] of Suit, drawable id of its [icon], and
 *  drawable id of [emptyIcon] which is shown when its Foundation pile is empty.
 */
enum class Suits(
    @StringRes val suit: Int,
    val color: Color,
    @DrawableRes val icon: Int,
    @DrawableRes val emptyIcon: Int
) {
    CLUBS(R.string.suits_clubs, Color.Black, R.drawable.suit_club, R.drawable.foundation_club_empty),
    DIAMONDS(R.string.suits_diamonds, Color.Red, R.drawable.suit_diamond, R.drawable.foundation_diamond_empty),
    HEARTS(R.string.suits_hearts, Color.Red, R.drawable.suit_heart, R.drawable.foundation_heart_empty),
    SPADES(R.string.suits_spades, Color.Black, R.drawable.suit_spade, R.drawable.foundation_spade_empty)
}

/**
 *  Enum class containing both possibilities the user has when resetting the game.
 */
enum class ResetOptions {
    RESTART,
    NEW
}

/**
 *  Enum class containing the [gameName]'s string resource id of all available games.
 */
enum class Games(@StringRes val gameName: Int) {
    KLONDIKETURNONE(R.string.games_klondike_turn_one)
}