package com.heyzeusv.solitaire.service

import androidx.annotation.Keep
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.games.Games

@Keep
data class FsGameStats(
    val game: String = "",
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val lowestMoves: Int = 9999,
    val totalMoves: Int = 0,
    val fastestWin: Long = 359999L,
    val totalTime: Long = 0,
    val totalScore: Int = 0,
    val bestCombinedScore: Long = 99999L
) {
    infix fun combineWith(gs: FsGameStats): FsGameStats {
        return FsGameStats(
            game = game,
            gamesPlayed = gamesPlayed.plus(gs.gamesPlayed),
            gamesWon = gamesWon.plus(gs.gamesWon),
            lowestMoves = lowestMoves.coerceAtMost(gs.lowestMoves),
            totalMoves = totalMoves.plus(gs.totalMoves),
            fastestWin = fastestWin.coerceAtMost(gs.fastestWin),
            totalTime = totalTime.plus(gs.totalTime),
            totalScore = totalScore.plus(gs.totalScore),
            bestCombinedScore = bestCombinedScore.coerceAtMost(gs.bestCombinedScore)
        )
    }

    fun toGameStats(): GameStats = GameStats.newBuilder().also { gs ->
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
}

fun GameStats.toFsGameStats(): FsGameStats = FsGameStats(
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

fun List<GameStats>.toFsGameStatsList(): List<FsGameStats> = map { it.toFsGameStats() }

fun List<FsGameStats>.toGameStatsList(): List<GameStats> = map { it.toGameStats() }
