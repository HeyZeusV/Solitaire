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
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.ui.board.GameViewModel
import com.heyzeusv.solitaire.ui.board.games.Easthaven
import com.heyzeusv.solitaire.ui.board.scoreboard.ScoreboardLogic
import com.heyzeusv.solitaire.ui.toolbar.menu.GamesMenu
import com.heyzeusv.solitaire.ui.toolbar.menu.RulesMenu
import com.heyzeusv.solitaire.ui.toolbar.menu.StatsMenu
import com.heyzeusv.solitaire.util.icons.Games
import com.heyzeusv.solitaire.util.icons.Rules
import com.heyzeusv.solitaire.util.icons.Stats
import com.heyzeusv.solitaire.util.theme.FoundationRules
import com.heyzeusv.solitaire.util.theme.StockRules
import com.heyzeusv.solitaire.util.theme.TableauRules
import com.heyzeusv.solitaire.util.theme.WasteRules

/**
 *  Enum class containing the 4 possible suits in a game of Solitaire with additional information.
 *  Includes [suit] in String Resource form, [color] of Suit, drawable id of its [icon], the
 *  drawable id of [emptyIcon] which is shown when its Foundation pile is empty.
 */
enum class Suits(
    @StringRes val suit: Int,
    val color: Color,
    @DrawableRes val icon: Int,
    @DrawableRes val emptyIcon: Int
) {
    CLUBS(
        R.string.suits_clubs,
        Color.Black,
        R.drawable.suit_club,
        R.drawable.foundation_club_empty
    ),
    DIAMONDS(
        R.string.suits_diamonds,
        Color.Red,
        R.drawable.suit_diamond,
        R.drawable.foundation_diamond_empty
    ),
    HEARTS(
        R.string.suits_hearts,
        Color.Red,
        R.drawable.suit_heart,
        R.drawable.foundation_heart_empty
    ),
    SPADES(
        R.string.suits_spades,
        Color.Black,
        R.drawable.suit_spade,
        R.drawable.foundation_spade_empty
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
 *  Enum class that holds the possible [amount] of cards that are drawn at a time when user clicks
 *  on [Stock] pile.
 */
enum class DrawAmount(val amount: Int) {
    Zero(0),
    One(1),
    Three(3),
    Seven(7),
    Ten(10)
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
 *  Enum class that holds possible starting scores a [Game] can have. [amount] will be used by
 *  [ScoreboardLogic].
 */
enum class StartingScore(val amount: Int) {
    Zero(0),
    One(1),
    Four(4)
}

/**
 *  Enum class that holds the highest score a player can obtain in a [Games]. [amount] will be
 *  displayed on [StatsMenu] and to calculate average score percentage.
 */
enum class MaxScore(val amount: Int) {
    OneDeck(52),
    TwoDecks(104)
}

/**
 *  Each game starts with a certain [amount] of Cards face up on each [Tableau] pile.
 */
enum class ResetFaceUpAmount(val amount: Int) {
    One(1),
    Four(4),
    Five(5),
    Seven(7)
}

/**
 *  Used to tell how many [Foundation] and [Tableau] piles currently selected [Games] uses.
 */
enum class NumberOfPiles(val amount: Int) {
    Four(4),
    Seven(7),
    Eight(8),
    Ten(10)
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
    FullPileScore,
    Illegal
}

/**
 *  Enum class that represents possible states when user interacts with Menu Button. [Buttons] is
 *  the state when user presses Menu Button. [ButtonsFromScreen] is the state when the user closes
 *  a Menu Screen. [Games], [Rules], [Stats], [Settings] and [About] refer to the possible Menu
 *  Screens user can open when pressing their respective Buttons. They hold resource ids that are
 *  used for their Buttons.
 */
enum class MenuState(
    @StringRes val nameId: Int = 0,
    val icon: ImageVector = Icons.Filled.ThumbUp,
    @StringRes val iconDescId: Int = 0
) {
    Buttons,
    ButtonsFromScreen,
    Games(R.string.menu_button_games, Icons.Filled.Games, R.string.menu_cdesc_games),
    Rules(R.string.menu_button_rules, Icons.Filled.Rules, R.string.menu_cdesc_rules),
    Stats(R.string.menu_button_stats, Icons.Filled.Stats, R.string.menu_cdesc_stats),
    Settings(R.string.menu_button_settings, Icons.Filled.Settings, R.string.menu_cdesc_settings),
    About(R.string.menu_button_about, Icons.Filled.Info, R.string.menu_cdesc_about)
}

/**
 *  Used by animations to determine start/end positions. Each entry except [TableauAll] is
 *  associated to a single pile. [TableauAll] is only used when playing [Easthaven] when
 *  clicking on Stock.
 */
enum class GamePiles {
    Stock,
    Waste,
    FoundationClubsOne,
    FoundationDiamondsOne,
    FoundationHeartsOne,
    FoundationSpadesOne,
    FoundationClubsTwo,
    FoundationDiamondsTwo,
    FoundationHeartsTwo,
    FoundationSpadesTwo,
    TableauZero,
    TableauOne,
    TableauTwo,
    TableauThree,
    TableauFour,
    TableauFive,
    TableauSix,
    TableauSeven,
    TableauEight,
    TableauNine,
    TableauAll
}

/**
 *  Used on [RulesMenu] to display each pile name in different colors that match pile colors found
 *  in each game's rule image.
 */
enum class PileInfo(
    @StringRes val nameId: Int,
    val color: Color
) {
    Stock(R.string.rules_stock, StockRules),
    Waste(R.string.rules_waste, WasteRules),
    Foundation(R.string.rules_foundation, FoundationRules),
    Tableau(R.string.rules_tableau, TableauRules)
}