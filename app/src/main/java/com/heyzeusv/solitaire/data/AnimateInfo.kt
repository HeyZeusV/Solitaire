package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.util.GamePiles

data class AnimateInfo(
    val start: GamePiles,
    val end: GamePiles,
    val animatedCards: List<Card>,
    val startTableauIndices: List<Int> = List(7) { 0 },
    val endTableauIndices: List<Int> = List(7) { 0 },
    val flipCardInfo: FlipCardInfo = FlipCardInfo.NoFlip,
    val stockWasteMove: Boolean = false,
    val tableauCardFlipInfo: TableauCardFlipInfo? = null,
    val undoAnimation: Boolean = false
) {
    var actionBeforeAnimation: suspend () -> Unit = { }
    var actionAfterAnimation: suspend () -> Unit = { }

    fun getUndoAnimateInfo(): AnimateInfo = AnimateInfo(
        start = end,
        end = start,
        animatedCards = animatedCards,
        startTableauIndices = endTableauIndices,
        endTableauIndices = startTableauIndices,
        flipCardInfo = flipCardInfo.getUndoFlipCardInfo(),
        stockWasteMove = stockWasteMove,
        tableauCardFlipInfo = tableauCardFlipInfo?.copy(
            flipCardInfo = tableauCardFlipInfo.flipCardInfo.getUndoFlipCardInfo()
        ),
        undoAnimation = true
    )

    fun isNotMultiPile(): Boolean = start != GamePiles.TableauAll && end != GamePiles.TableauAll
}

data class TableauCardFlipInfo(
    val flipCard: Card,
    val flipCardIndex: Int,
    val flipCardInfo: FlipCardInfo,
    val remainingPile: List<Card>
)