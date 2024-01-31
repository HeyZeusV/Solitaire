package com.heyzeusv.solitaire

/**
 *  Holds all Stats that are being stored in Proto DataStore [StatPreferences]
 */
data class Stats(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val lowestMoves: Int = 0,
    val averageMoves: Int = 0,
    val totalMoves: Int = 0,
    val fastestWin: Long = 0L,
    val averageTime: Long = 0L,
    val totalTime: Long = 0L,
    val averageScore: Int = 0,
    val bestTotalScore: Long = 0L
)

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