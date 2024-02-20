package com.heyzeusv.solitaire.ui.scoreboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.ui.AutoSizeText
import com.heyzeusv.solitaire.util.formatTimeDisplay

/**
 *  Composable that displays current game stats to the user
 */
@Composable
fun SolitaireScoreboard(
    sbVM: ScoreboardViewModel,
    modifier: Modifier = Modifier
) {
    // stats
    val moves by sbVM.moves.collectAsState()
    val timer by sbVM.time.collectAsState()
    val score by sbVM.score.collectAsState()

    // start timer once user makes a move
    if (moves == 1 && sbVM.jobIsCancelled()) sbVM.startTimer()

    SolitaireScoreboard(
        modifier = modifier,
        moves = moves,
        timer = timer,
        score = score
    )
}

/**
 *  Composable that displays current game stats to the user. Displays the number of [moves] the user
 *  has taken, [timer] is how long the user has played the current game with, and [score] refers to
 *  the number of cards the user has placed in a Foundation pile.
 */
@Composable
fun SolitaireScoreboard(
    modifier: Modifier = Modifier,
    moves: Int = 0,
    timer: Long = 0L,
    score: Int = 0
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("Scoreboard"),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AutoSizeText(
            text = stringResource(R.string.scoreboard_stat_moves, moves),
            modifier = Modifier.weight(1f),
            color = Color.White,
            alignment = Alignment.Center
        )
        AutoSizeText(
            text = stringResource(R.string.scoreboard_stat_time, timer.formatTimeDisplay()),
            modifier = Modifier.weight(1f),
            color = Color.White,
            alignment = Alignment.Center
        )
        AutoSizeText(
            text = stringResource(R.string.scoreboard_stat_score, score),
            modifier = Modifier.weight(1f),
            color = Color.White,
            alignment = Alignment.Center
        )
    }
}

@Preview
@Composable
fun SolitaireScoreboardPreview() {
    SolitairePreview {
        SolitaireScoreboard(
            modifier = Modifier.height(100.dp),
            moves = 100,
            timer = 100000,
            score = 15
        )
    }
}