package com.heyzeusv.solitaire.scoreboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.board.GameViewModel
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.formatTimeDisplay
import com.heyzeusv.solitaire.util.theme.scoreboardText

/**
 *  Composable that displays current game stats to the user
 */
@Composable
fun SolitaireScoreboard(
    gameVM: GameViewModel,
    modifier: Modifier = Modifier
) {
    // stats
    val moves by gameVM.sbLogic.moves.collectAsState()
    val timer by gameVM.sbLogic.time.collectAsState()
    val score by gameVM.sbLogic.score.collectAsState()

    val autoCompleteActive by gameVM.autoCompleteActive.collectAsState()

    // start timer once user makes a move
    if (moves == 1 && gameVM.sbLogic.jobIsCancelled()) gameVM.sbLogic.startTimer()

    // stop time once autocomplete starts
    LaunchedEffect(key1 = autoCompleteActive) {
        if (autoCompleteActive) gameVM.sbLogic.pauseTimer()
    }

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
            .height(88.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("Scoreboard"),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.scoreboard_stat_moves, moves),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.scoreboardText
        )
        Text(
            text = stringResource(R.string.scoreboard_stat_time, timer.formatTimeDisplay()),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.scoreboardText
        )
        Text(
            text = stringResource(R.string.scoreboard_stat_score, score),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.scoreboardText
        )
    }
}

@Preview
@Composable
fun SolitaireScoreboardPreview() {
    SolitairePreview {
        SolitaireScoreboard(
            modifier = Modifier,
            moves = 100,
            timer = 100000,
            score = 15
        )
    }
}