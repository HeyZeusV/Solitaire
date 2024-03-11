package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.util.GamePiles

data class AnimateInfo(
    val start: GamePiles,
    val end: GamePiles,
    val cards: List<Card>,
    val startIndex: Int = 0,
    val endIndex: Int = 0
)