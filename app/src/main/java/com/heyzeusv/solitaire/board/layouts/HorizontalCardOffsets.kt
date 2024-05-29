package com.heyzeusv.solitaire.board.layouts

import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.board.HorizontalCardPileWithFlip

/**
 *  Contains offsets needed by [HorizontalCardPileWithFlip] animations which is used for animations
 *  between Stock and Waste piles.
 */
data class HorizontalCardOffsets(
    private val rightCardStartOffset: IntOffset,
    private val rightCardEndOffset: IntOffset,
    private val middleCardStartOffset: IntOffset,
    private val middleCardEndOffset: IntOffset,
    private val leftCardStartOffset: IntOffset,
    private val leftCardEndOffset: IntOffset
) {
    val startOffsets = listOf(rightCardStartOffset, middleCardStartOffset, leftCardStartOffset)
    val endOffsets = listOf(rightCardEndOffset, middleCardEndOffset, leftCardEndOffset)
}