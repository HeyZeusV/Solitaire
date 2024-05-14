package com.heyzeusv.solitaire.service

data class SingleGameStats(
    val game: String,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val lowestMoves: Int,
    val totalMoves: Int,
    val fastestWin: Long,
    val totalTime: Long,
    val totalScore: Int,
    val bestCombinedScore: Long
)