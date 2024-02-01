package com.heyzeusv.solitaire.data

import java.text.DecimalFormat

/**
 *  Holds all Stats that are being stored in Proto DataStore StatPreferences.
 */
data class Stats(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val lowestMoves: Int = 999999,
    val averageMoves: Int = 0,
    val totalMoves: Int = 0,
    val fastestWin: Long = 359999L,
    val averageTime: Long = 0L,
    val totalTime: Long = 0L,
    val totalScore: Int = 0,
    val bestTotalScore: Long = 999999L
) {
    private val gamePercent: Double = if (gamesPlayed == 0) {
        0.0
    } else {
        (gamesWon.toDouble() / gamesPlayed) * 100
    }
    val averageScore: Int = if (gamesPlayed == 0) 0 else (totalScore / gamesPlayed)
    private val scorePercent: Double = (averageScore.toDouble() / 52) * 100
    private val df = DecimalFormat("#.##")

    fun getGamePercent(): String = df.format(gamePercent)
    fun getScorePercent(): String = df.format(scorePercent)
}

/**
 *  Holds stats for recently finished game, which will be used to calculate updated [Stats].
 */
data class LastGameStats(
    val gameWon: Boolean,
    val moves: Int,
    val time: Long,
    val score: Int
) {
    val totalScore: Long = moves + time + score
}