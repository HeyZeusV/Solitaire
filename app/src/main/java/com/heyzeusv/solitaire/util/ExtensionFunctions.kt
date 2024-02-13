package com.heyzeusv.solitaire.util

import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.data.Card
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

fun GameStats.getScorePercentage(): String {
    val df = DecimalFormat("#.##")
    val scorePercent: Double = (getAverageScore().toDouble() / 52) * 100
    return df.format(scorePercent)
}

/**
 *  Used to compare Pile lists which are SnapshotStateList internally but displayed as Lists,
 *  so need to cast to List when comparing.
 */
fun List<Card>.isNotEqual(list: List<Card>): Boolean = this.toList() != list.toList()