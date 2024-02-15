package com.heyzeusv.solitaire.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.heyzeusv.solitaire.Game
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
 *  Enum class containing the [gameName]'s string resource id of all available games and [drawAmount]
 *  which is the amount of Cards drawn at a time.
 */
enum class Games(
    @StringRes val gameName: Int,
    val drawAmount: Int,
    val dataStoreEnum: Game
) {
    KLONDIKETURNONE(R.string.games_klondike_turn_one, 1, Game.GAME_KLONDIKETURNONE),
    KLONDIKETURNTHREE(R.string.games_klondike_turn_three, 3, Game.GAME_KLONDIKETURNTHREE)
}

/**
 *  Enum class that will be used to determine Scoreboard action after user makes a move. [MOVE] will
 *  only increase moves value, [MOVE_SCORE] will increase both moves and score values,
 *  [MOVE_MINUS_SCORE] increases moves but decreases score, and nothing
 *  will change when [ILLEGAL].
 */
enum class MoveResult {
    MOVE,
    MOVE_SCORE,
    MOVE_MINUS_SCORE,
    ILLEGAL
}