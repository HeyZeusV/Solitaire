package com.heyzeusv.solitaire.service

import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.games.Games

data class SingleGameStats(
    val game: String = "",
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val lowestMoves: Int = 9999,
    val totalMoves: Int = 0,
    val fastestWin: Long = 359999L,
    val totalTime: Long = 0,
    val totalScore: Int = 0,
    val bestCombinedScore: Long = 99999L
)

fun GameStats.toSingleGameStats(): SingleGameStats = SingleGameStats(
    game = Games.getGameClass(game).dbName,
    gamesPlayed = gamesPlayed,
    gamesWon = gamesWon,
    lowestMoves = lowestMoves,
    totalMoves = totalMoves,
    fastestWin = fastestWin,
    totalTime = totalTime,
    totalScore = totalScore,
    bestCombinedScore = bestCombinedScore
)

fun List<GameStats>.toSingleGameStatsList(): List<SingleGameStats> = map { it.toSingleGameStats() }