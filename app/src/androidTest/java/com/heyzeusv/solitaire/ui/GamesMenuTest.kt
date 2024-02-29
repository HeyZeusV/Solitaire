package com.heyzeusv.solitaire.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.ui.tools.GamesMenu
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.onLazyListScrollToNode
import com.heyzeusv.solitaire.util.onNodeWithTextId
import com.heyzeusv.solitaire.util.theme.Purple40
import com.heyzeusv.solitaire.util.theme.SolitaireTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GamesMenuTest {

    @get:Rule
    var composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun gamesMenu_display() {
        var surfaceColor: Color = Color.White
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    surfaceColor = MaterialTheme.colorScheme.surface
                    GamesMenu(
                        updateStats = { },
                        lgs = LastGameStats(false, 0, 0L, 0),
                        selectedGame = Games.KLONDIKE_TURN_ONE,
                        onBackPress = { }
                    )
                }
            }

            onNodeWithTextId(R.string.menu_button_games).assertIsDisplayed()
            Games.entries.forEach { game ->
                onLazyListScrollToNode("Games Menu List", game.nameId)
                if (game == Games.KLONDIKE_TURN_ONE) {
                    checkBackgroundColor("${game.name} Card", Purple40)
                } else {
                    checkBackgroundColor("${game.name} Card", surfaceColor)
                }
            }

        }
    }

    /**
     *  Checks that node with given [nodeTestTag] has given [color] as its background color.
     */
    private fun checkBackgroundColor(nodeTestTag: String, color: Color) {
        val array = IntArray(20)
        composeRule.apply {
            onNode(hasTestTag(nodeTestTag)).captureToImage()
                .readPixels(array, startX = 10, startY = 10, width = 5, height = 4)
            array.forEach { assertEquals(color.convert(ColorSpaces.Srgb).hashCode(), it) }
        }
    }
}