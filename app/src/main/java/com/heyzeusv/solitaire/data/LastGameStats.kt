package com.heyzeusv.solitaire.data

/**
 *  Holds stats for recently finished game, which will be used to calculate updated GameStats.
 */
data class LastGameStats(
    val gameWon: Boolean,
    val moves: Int,
    val time: Long,
    val score: Int
) {
    val totalScore: Long = moves + time + score
}