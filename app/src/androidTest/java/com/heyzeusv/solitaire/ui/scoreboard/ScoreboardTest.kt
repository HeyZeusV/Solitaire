package com.heyzeusv.solitaire.ui.scoreboard

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.scoreboard.SolitaireScoreboard
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.onNodeWithTextId
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScoreboardTest {

    @get:Rule
    var composeRule = createAndroidComposeRule<ComponentActivity>()

    private val modifier = Modifier.height(100.dp)

    @Test
    fun scoreboard_initial() {
        composeRule.apply {
            setContent {
                SolitairePreview {
                    SolitaireScoreboard(modifier = modifier)
                }
            }

            onNodeWithTextId(R.string.scoreboard_stat_moves, 0).assertIsDisplayed()
            onNodeWithTextId(R.string.scoreboard_stat_time, "00:00").assertIsDisplayed()
            onNodeWithTextId(R.string.scoreboard_stat_score, 0).assertIsDisplayed()
        }
    }

    @Test
    fun scoreboard_timeFormatted() {
        composeRule.apply {
            setContent {
                SolitairePreview {
                    SolitaireScoreboard(
                        modifier = modifier,
                        timer = 359999L
                    )
                }
            }

            onNodeWithTextId(R.string.scoreboard_stat_time, "5999:59").assertIsDisplayed()
        }
    }
}