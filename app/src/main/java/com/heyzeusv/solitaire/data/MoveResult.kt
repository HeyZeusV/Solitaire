package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.util.GamePiles

sealed class MoveResult {
    data class Move(val start: GamePiles, val end: GamePiles) : MoveResult()
    data class MoveScore(val start: GamePiles, val end: GamePiles) : MoveResult()
    data class MoveMinusScore(val start: GamePiles, val end: GamePiles) : MoveResult()
    data object Illegal : MoveResult()
}