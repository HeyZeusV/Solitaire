package com.heyzeusv.solitaire.service

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.games.Games

@Keep
data class SingleGameStats(
    @DocumentId val game: String = "",
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

fun SingleGameStats.toGameStats(): GameStats = GameStats.newBuilder().also { gs ->
    gs.game = Games.getDataStoreEnum(game)
    gs.gamesPlayed = gamesPlayed
    gs.gamesWon = gamesWon
    gs.lowestMoves = lowestMoves
    gs.totalMoves = totalMoves
    gs.fastestWin = fastestWin
    gs.totalTime = totalTime
    gs.totalScore = totalScore
    gs.bestCombinedScore = bestCombinedScore
}.build()

fun List<SingleGameStats>.toGameStatsList(): List<GameStats> = map { it.toGameStats() }
