package com.heyzeusv.solitaire.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.GameStats
import java.text.DecimalFormat

fun Long.formatTimeDisplay(): String {
    val minutes = this / 60
    val seconds = this % 60

    return String.format("%02d:%02d", minutes, seconds)
}

fun Long.formatTimeStats(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    val hourString = if (hours == 0L) "" else "$hours hours, "

    return "$hourString$minutes minutes, $seconds seconds"
}

fun GameStats.getWinPercentage(): String {
    val df = DecimalFormat("#.##")
    val gamePercent: Double = if (gamesPlayed == 0) {
        0.0
    } else {
        (gamesWon.toDouble() / gamesPlayed) * 100
    }
    return df.format(gamePercent)
}

fun GameStats.getAverageMoves(): Int = if (gamesPlayed == 0) 0 else (totalMoves / gamesPlayed)

fun GameStats.getAverageTime(): Long = if (gamesPlayed == 0) 0L else (totalTime / gamesPlayed)

fun GameStats.getAverageScore(): Int = if (gamesPlayed == 0) 0 else (totalScore / gamesPlayed)

fun GameStats.getScorePercentage(maxScore: MaxScore): String {
    val df = DecimalFormat("#.##")
    val scorePercent: Double = (getAverageScore().toDouble() / maxScore.amount) * 100
    return df.format(scorePercent)
}

fun IntOffset.plusX(x: Int) = IntOffset(this.x + x, this.y)

@Composable
fun Int.toDp() = with(LocalDensity.current) { this@toDp.toDp() }

// https://stackoverflow.com/a/69146178
fun Modifier.gesturesDisabled(disabled: Boolean = true) =
    if (disabled) {
        pointerInput(Unit) {
            awaitPointerEventScope {
                // we should wait for all new pointer events
                while (true) {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach(PointerInputChange::consume)
                }
            }
        }
    } else {
        this
    }