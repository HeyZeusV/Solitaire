package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.util.GamePiles

data class AnimateInfo(
    val start: GamePiles,
    val end: GamePiles,
    val animatedCards: List<Card>,
    val startTableauIndex: Int = 0,
    val endTableauIndex: Int = 0,
    val flipAnimatedCards: FlipCardInfo = FlipCardInfo.NoFlip,
    val tableauCardFlipInfo: TableauCardFlipInfo? = null
) {
    var actionBeforeAnimation: () -> Unit = { }
    var actionAfterAnimation: () -> Unit = { }

    fun getUndoAnimateInfo(): AnimateInfo = AnimateInfo(
        start = end,
        end = start,
        animatedCards = animatedCards,
        startTableauIndex = endTableauIndex,
        endTableauIndex = startTableauIndex,
        flipAnimatedCards = flipAnimatedCards.getUndoFlipCardInfo(),
        tableauCardFlipInfo = tableauCardFlipInfo?.copy(
            flipCardInfo = tableauCardFlipInfo.flipCardInfo.getUndoFlipCardInfo()
        )
    )
}

data class TableauCardFlipInfo(
    val card: Card,
    val cardIndex: Int,
    val flipCardInfo: FlipCardInfo,
    val remainingPile: List<Card>
)