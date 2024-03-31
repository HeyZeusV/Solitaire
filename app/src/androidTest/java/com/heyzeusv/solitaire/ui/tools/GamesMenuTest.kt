package com.heyzeusv.solitaire.ui.tools

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.toolbar.GamesMenu
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.onLazyListScrollToNode
import com.heyzeusv.solitaire.util.onNodeWithTextId
import com.heyzeusv.solitaire.util.theme.SolitaireTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GamesMenuTest {

    @get:Rule
    var composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun gamesMenu_display() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    GamesMenu(
                        gameSwitchConfirmOnClick = { },
                        gameInfoOnClickCheck = { false },
                        selectedGame = Games.KLONDIKE_TURN_ONE,
                        onBackPress = { }
                    )
                }
            }

            onNodeWithTextId(R.string.menu_button_games).assertIsDisplayed()
            Games.entries.forEach { game ->
                onLazyListScrollToNode("Games Menu List", game.nameId)
                if (game == Games.KLONDIKE_TURN_ONE) {
                    onNode(hasTestTag("${game.name} Card true"))
                } else {
                    onNode(hasTestTag("${game.name} Card false"))
                }
            }
        }
    }

    @Test
    fun gamesMenu_switchGames() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    GamesMenu(
                        gameSwitchConfirmOnClick = { },
                        gameInfoOnClickCheck = { false },
                        selectedGame = Games.KLONDIKE_TURN_ONE,
                        onBackPress = { }
                    )
                }
            }

            // switch to Alaska and check backgrounds
            onLazyListScrollToNode("Games Menu List", Games.ALASKA.nameId)
            onNodeWithTextId(Games.ALASKA.nameId).performClick()
            Games.entries.forEach { game ->
                onLazyListScrollToNode("Games Menu List", game.nameId)
                if (game == Games.ALASKA) {
                    onNode(hasTestTag("${game.name} Card true"))

                } else {
                    onNode(hasTestTag("${game.name} Card false"))
                }
            }

            // switch to Australian Patience and check backgrounds
            onLazyListScrollToNode("Games Menu List", Games.AUSTRALIAN_PATIENCE.nameId)
            onNodeWithTextId(Games.AUSTRALIAN_PATIENCE.nameId).performClick()
            Games.entries.forEach { game ->
                onLazyListScrollToNode("Games Menu List", game.nameId)
                if (game == Games.AUSTRALIAN_PATIENCE) {
                    onNode(hasTestTag("${game.name} Card true"))

                } else {
                    onNode(hasTestTag("${game.name} Card false"))
                }
            }
        }
    }

    @Test
    fun gamesMenu_switchGames_midGame_confirm() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    GamesMenu(
                        gameSwitchConfirmOnClick = { },
                        gameInfoOnClickCheck = { true },
                        selectedGame = Games.KLONDIKE_TURN_ONE,
                        onBackPress = { }
                    )
                }
            }

            // switch to Alaska
            onLazyListScrollToNode("Games Menu List", Games.ALASKA.nameId)
            onNodeWithTextId(Games.ALASKA.nameId).performClick()

            // confirm game switch
            onNodeWithTextId(R.string.games_ad_title).assertIsDisplayed()
            onNodeWithTextId(R.string.games_ad_confirm).performClick()

            Games.entries.forEach { game ->
                onLazyListScrollToNode("Games Menu List", game.nameId)
                if (game == Games.ALASKA) {
                    onNode(hasTestTag("${game.name} Card true"))
                } else {
                    onNode(hasTestTag("${game.name} Card false"))
                }
            }
        }
    }

    @Test
    fun gamesMenu_switchGames_midGame_dismiss() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    GamesMenu(
                        gameSwitchConfirmOnClick = { },
                        gameInfoOnClickCheck = { true },
                        selectedGame = Games.KLONDIKE_TURN_ONE,
                        onBackPress = { }
                    )
                }
            }

            // switch to Alaska
            onLazyListScrollToNode("Games Menu List", Games.ALASKA.nameId)
            onNodeWithTextId(Games.ALASKA.nameId).performClick()

            // confirm game switch
            onNodeWithTextId(R.string.games_ad_title).assertIsDisplayed()
            onNodeWithTextId(R.string.games_ad_dismiss).performClick()

            Games.entries.forEach { game ->
                onLazyListScrollToNode("Games Menu List", game.nameId)
                if (game == Games.KLONDIKE_TURN_ONE) {
                    onNode(hasTestTag("${game.name} Card true"))
                } else {
                    onNode(hasTestTag("${game.name} Card false"))
                }
            }
        }
    }
}