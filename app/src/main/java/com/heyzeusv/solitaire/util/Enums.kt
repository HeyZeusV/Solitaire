package com.heyzeusv.solitaire.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.ui.board.GameViewModel
import com.heyzeusv.solitaire.ui.toolbar.menu.GamesMenu
import com.heyzeusv.solitaire.ui.toolbar.menu.StatsMenu
import com.heyzeusv.solitaire.util.Redeals.*
import com.heyzeusv.solitaire.util.icons.Games
import com.heyzeusv.solitaire.util.icons.Stats

/**
 *  Enum class containing the 4 possible suits in a game of Solitaire with additional information.
 *  Includes [suit] in String Resource form, [color] of Suit, drawable id of its [icon], the
 *  drawable id of [emptyIcon] which is shown when its Foundation pile is empty, and the [GamePiles]
 *  associated to it by given [gamePile].
 */
enum class Suits(
    @StringRes val suit: Int,
    val color: Color,
    @DrawableRes val icon: Int,
    @DrawableRes val emptyIcon: Int,
    val gamePile: GamePiles
) {
    CLUBS(
        R.string.suits_clubs,
        Color.Black,
        R.drawable.suit_club,
        R.drawable.foundation_club_empty,
        GamePiles.ClubsFoundation
    ),
    DIAMONDS(
        R.string.suits_diamonds,
        Color.Red,
        R.drawable.suit_diamond,
        R.drawable.foundation_diamond_empty,
        GamePiles.DiamondsFoundation
    ),
    HEARTS(
        R.string.suits_hearts,
        Color.Red,
        R.drawable.suit_heart,
        R.drawable.foundation_heart_empty,
        GamePiles.HeartsFoundation
    ),
    SPADES(
        R.string.suits_spades,
        Color.Black,
        R.drawable.suit_spade,
        R.drawable.foundation_spade_empty,
        GamePiles.SpadesFoundation
    )
}

/**
 *  Enum class containing both possibilities the user has when resetting the game.
 */
enum class ResetOptions {
    RESTART,
    NEW
}

/**
 *  Enum class containing the [nameId]'s string resource id of all available games and
 *  [drawAmount] which is the amount of Cards drawn at a time. It also contains the Proto DataStore
 *  enum value [dataStoreEnum] which is used to save stats to correct game.
 */
enum class Games(
    @StringRes val nameId: Int,
    @StringRes val familyId: Int,
    @DrawableRes val iconId: Int,
    val redeals: Redeals,
    val drawAmount: Int,
    val dataStoreEnum: Game,
    val maxScore: MaxScore = MaxScore.ONE_DECK
    ) {
    KLONDIKE_TURN_ONE(
        R.string.games_klondike_turn_one,
        R.string.games_family_klondike,
        R.drawable.preview_klondike_turn_one,
        Unlimited,
        1,
        Game.GAME_KLONDIKETURNONE
    ),
    KLONDIKE_TURN_THREE(
        R.string.games_klondike_turn_three,
        R.string.games_family_klondike,
        R.drawable.preview_klondike_turn_three,
        Unlimited,
        3,
        Game.GAME_KLONDIKETURNTHREE
    ),
    CLASSIC_WESTCLIFF(
        R.string.games_classic_westcliff,
        R.string.games_family_klondike,
        R.drawable.preview_classic_westcliff,
        None,
        1,
        Game.GAME_CLASSIC_WESTCLIFF,
        MaxScore.ONE_DECK_NO_ACES
    ),
    EASTHAVEN(
        R.string.games_easthaven,
        R.string.games_family_klondike,
        R.drawable.preview_easthaven,
        None,
        0,
        Game.GAME_EASTHAVEN
    ),
    YUKON(
        R.string.games_yukon,
        R.string.games_family_yukon,
        R.drawable.preview_yukon,
        None,
        0,
        Game.GAME_YUKON
    ),
    ALASKA(
        R.string.games_alaska,
        R.string.games_family_yukon,
        R.drawable.preview_yukon,
        None,
        0,
        Game.GAME_ALASKA
    ),
    RUSSIAN(
        R.string.games_russian,
        R.string.games_family_yukon,
        R.drawable.preview_yukon,
        None,
        0,
        Game.GAME_RUSSIAN
    ),
    AUSTRALIAN_PATIENCE(
        R.string.games_australian_patience,
        R.string.games_family_yukon,
        R.drawable.preview_australian_patience,
        None,
        1,
        Game.GAME_AUSTRALIAN_PATIENCE
    ),
    CANBERRA(
        R.string.games_canberra,
        R.string.games_family_yukon,
        R.drawable.preview_australian_patience,
        Once,
        1,
        Game.GAME_CANBERRA
    )
}

/**
 *  Enum class that holds the possible [amount] of cards that are drawn at a time when user clicks
 *  on [Stock] pile.
 */
enum class DrawAmount(val amount: Int) {
    Zero(0),
    One(1),
    Three(3),
    Seven(7)
}

/**
 *  Enum class that holds the possible amount of redeals a [Game] can have. [nameId] is used to
 *  display redeal amount on [GamesMenu]. [amount] will be used by [GameViewModel] for redeal
 *  logic.
 */
enum class Redeals(
    @StringRes val nameId: Int,
    val amount: Int
) {
    None(R.string.redeal_none, 0),
    Once(R.string.redeal_once, 1),
    Unlimited(R.string.redeal_unlimited, Int.MAX_VALUE)
}

/**
 *  Enum class that holds the highest score a player can obtain in a [Games]. [amount] will be
 *  displayed on [StatsMenu] and to calculate average score percentage.
 */
enum class MaxScore(val amount: Int) {
    ONE_DECK(52),
    ONE_DECK_NO_ACES(48)
}

/**
 *  Enum class that will be used to determine Scoreboard action after user makes a move. [Move] will
 *  only increase moves value, [MoveScore] will increase both moves and score values,
 *  [MoveMinusScore] increases moves but decreases score, and nothing
 *  will change when [Illegal].
 */
enum class MoveResult {
    Move,
    MoveScore,
    MoveMinusScore,
    Illegal
}

/**
 *  Enum class that represents possible states when user interacts with Menu Button. [Buttons] is
 *  the state when user presses Menu Button. [ButtonsFromScreen] is the state when the user closes
 *  a Menu Screen. [Games], [Stats], [Settings] and [About] refer to the possible Menu Screens user
 *  can open when pressing their respective Buttons. They hold resource ids that are used for their
 *  Buttons.
 */
enum class MenuState(
    @StringRes val nameId: Int = 0,
    val icon: ImageVector = Icons.Filled.ThumbUp,
    @StringRes val iconDescId: Int = 0
) {
    Buttons,
    ButtonsFromScreen,
    Games(R.string.menu_button_games, Icons.Filled.Games, R.string.menu_cdesc_games),
    Stats(R.string.menu_button_stats, Icons.Filled.Stats, R.string.menu_cdesc_stats),
    Settings(R.string.menu_button_settings, Icons.Filled.Settings, R.string.menu_cdesc_settings),
    About(R.string.menu_button_about, Icons.Filled.Info, R.string.menu_cdesc_about)
}

/**
 *  Used by animations to determine start/end positions. Each entry except [TableauAll] is
 *  associated to a single pile. [TableauAll] is only used when playing [Games.EASTHAVEN] when
 *  clicking on Stock.
 */
enum class GamePiles {
    Stock,
    Waste,
    ClubsFoundation,
    DiamondsFoundation,
    HeartsFoundation,
    SpadesFoundation,
    TableauZero,
    TableauOne,
    TableauTwo,
    TableauThree,
    TableauFour,
    TableauFive,
    TableauSix,
    TableauAll
}