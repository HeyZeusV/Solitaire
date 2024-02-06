package com.heyzeusv.solitaire

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.ui.SolitaireScoreboard
import com.heyzeusv.solitaire.util.SolitairePreview
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
        composeRule.setContent {
            SolitairePreview {
                SolitaireScoreboard(modifier = modifier)
            }
        }

        composeRule.onNodeWithTextId(R.string.scoreboard_stat_moves, 0).assertExists()
        composeRule.onNodeWithTextId(R.string.scoreboard_stat_time, "00:00").assertExists()
        composeRule.onNodeWithTextId(R.string.scoreboard_stat_score, 0).assertExists()
    }

    @Test
    fun scoreboard_timeFormatted() {
        composeRule.setContent {
            SolitairePreview {
                SolitaireScoreboard(
                    modifier = modifier,
                    timer = 359999L
                )
            }
        }

        composeRule.onNodeWithTextId(R.string.scoreboard_stat_time, "1439:59")
    }
}