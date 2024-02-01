package com.heyzeusv.solitaire.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.autosizetext.AutoSizeText
import com.heyzeusv.solitaire.util.formatTimeDisplay

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
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AutoSizeText(
            text = "Moves\n$moves",
            modifier = Modifier.weight(1f),
            color = Color.White,
            alignment = Alignment.Center
        )
        AutoSizeText(
            text = "Time\n${timer.formatTimeDisplay()}",
            modifier = Modifier.weight(1f),
            color = Color.White,
            alignment = Alignment.Center
        )
        AutoSizeText(
            text = "Score\n$score",
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