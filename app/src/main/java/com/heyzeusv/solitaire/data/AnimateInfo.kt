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
)

data class TableauCardFlipInfo(val card: Card, val cardIndex: Int, val remainingPile: List<Card>)